package krx.crawling.domain.webhooks.service;

import krx.crawling.domain.stocks.entity.Stock;

public interface WebhookService {
    public void sendPostRequest(String url, String json);
}
