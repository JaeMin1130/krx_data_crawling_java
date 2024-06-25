package krx.crawling.domain.webhooks.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import krx.crawling.domain.stocks.entity.Stock;

public class WebhookServiceImpl implements WebhookService {

    @Override
    public void sendPostRequest(String url, String json) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            // Synchronous request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response: " + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String stockToJson(Stock stock) {
        // You can use a library like Jackson or Gson to convert the object to JSON
        // For simplicity, we'll manually create the JSON string
        return String.format(
                "{\"company\":\"%s\", \"marketCategory\":\"%s\", \"sector\":\"%s\", \"close\":\"%s\", \"volume\":\"%s\", \"tradingValue\":\"%s\", \"marketCap\":\"%s\", \"eps\":\"%s\", \"per\":\"%s\", \"bps\":\"%s\", \"pbr\":\"%s\", \"dps\":\"%s\", \"dy\":\"%s\"}",
                stock.getCompany(), stock.getMarketCategory(), stock.getSector(), stock.getClose(), stock.getVolume(),
                stock.getTradingValue(), stock.getMarketCap(), stock.getEps(), stock.getPer(), stock.getBps(),
                stock.getPbr(), stock.getDps(), stock.getDy());
    }

    // private String stockToXml(Stock stock) {
    // return String.format(
    // "<stock>" +
    // "<company>%s</company>" +
    // "<marketCategory>%s</marketCategory>" +
    // "<sector>%s</sector>" +
    // "<close>%s</close>" +
    // "<volume>%s</volume>" +
    // "<tradingValue>%s</tradingValue>" +
    // "<marketCap>%s</marketCap>" +
    // "<eps>%s</eps>" +
    // "<per>%s</per>" +
    // "<bps>%s</bps>" +
    // "<pbr>%s</pbr>" +
    // "<dps>%s</dps>" +
    // "<dy>%s</dy>" +
    // "</stock>",
    // stock.getCompany(), stock.getMarketCategory(), stock.getSector(),
    // stock.getClose(), stock.getVolume(),
    // stock.getTradingValue(), stock.getMarketCap(), stock.getEps(),
    // stock.getPer(), stock.getBps(),
    // stock.getPbr(), stock.getDps(), stock.getDy());
    // }
}
