package com.crio.warmup.stock.portfolio;

import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  private RestTemplate restTemplate;
  private StockQuotesService stockQuotesService;
// Caution: Do not delete or modify the constructor, or else your build will break!
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  
  public PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }
  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF
  
  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate)
    {
    // TODO Auto-generated method stub
     List<AnnualizedReturn> annualizedReturnsList = new ArrayList<>();
     for(PortfolioTrade trade:portfolioTrades)
     {
       List<Candle> candles = new ArrayList<>();
       try {
         candles = stockQuotesService.getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate);
       } catch (JsonProcessingException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
       }
       if(candles.size() == 0)
        continue;
       
       Double sellPrice = candles.get(candles.size()-1).getClose();
       Double buyPrice = candles.get(0).getOpen();
       Double totalReturns = (sellPrice-buyPrice)/buyPrice;
       LocalDate purchase = trade.getPurchaseDate();
       Double noYears = purchase.until(endDate,ChronoUnit.DAYS)/365.24;
       Double annualized_returns = Math.pow(1+totalReturns, (1/noYears))-1;
       annualizedReturnsList.add(new AnnualizedReturn(trade.getSymbol(),annualized_returns,totalReturns));
     }
    Collections.sort(annualizedReturnsList, getComparator());
    return annualizedReturnsList;
  }


  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
    String Url = buildUri(symbol, from, to);
    TiingoCandle[] tc = restTemplate.getForObject(Url, TiingoCandle[].class);
    return Arrays.asList(tc);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       final String uriTemplate = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
            + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
       return uriTemplate;
  }

}
