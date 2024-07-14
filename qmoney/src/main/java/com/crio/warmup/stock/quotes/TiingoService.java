package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest

  private String buildUri(String symbol, LocalDate from, LocalDate to) {
    return "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=" + from
    + "&endDate=" + to + "&token=" + getToken();
  }

  private String getToken() {
    return "3a9bb71bc48d9077662424550e62c72a7b1ce2cd";
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException {
	// TODO Auto-generated method stub
	String Url = buildUri(symbol, from, to);
  TiingoCandle[] tc = restTemplate.getForObject(Url, TiingoCandle[].class);
  if(tc == null)
    return new ArrayList<>();
    
  List<Candle> listOfCandle = Arrays.asList(tc);
  Collections.sort(listOfCandle, (c1,c2) -> c1.getDate().compareTo(c2.getDate()));
  return listOfCandle;
 }


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.
  
}
