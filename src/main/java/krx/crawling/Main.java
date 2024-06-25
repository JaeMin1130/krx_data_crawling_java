package krx.crawling;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import krx.crawling.domain.stocks.entity.Stock;
import krx.crawling.domain.stocks.service.StockService;
import krx.crawling.domain.stocks.service.StockServiceImpl;
import krx.crawling.domain.webhooks.service.WebhookService;
import krx.crawling.domain.webhooks.service.WebhookServiceImpl;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        Set<Stock> stockSet = new TreeSet<>();
        // stockSet = KrxCrawler.execute();

        // Use StockRepository to handle database operations
        StockService stockService = new StockServiceImpl();
        stockService.insertCrawledStocks(stockSet);

        // Stock stock = stockService.getStockById(1);
        WebhookService webhookService = new WebhookServiceImpl();
        String url = "https://discordapp.com/api/webhooks/1255033445575426070/-ej_99Y6ac8bHD5PK8FJc8_-9KStaTg0MT_cUoWFe_juDl_ovArTx5cZ5WEN0YP50oOO";
        String json = "{\n" +
                "  \"username\": \"Webhook\",\n" +
                "  \"avatar_url\": \"https://i.imgur.com/4M34hi2.png\",\n" +
                "  \"content\": \"Text message. Up to 2000 characters.\",\n" +
                "  \"embeds\": [\n" +
                "    {\n" +
                "      \"author\": {\n" +
                "        \"name\": \"Birdie♫\",\n" +
                "        \"url\": \"https://www.reddit.com/r/cats/\",\n" +
                "        \"icon_url\": \"https://i.imgur.com/R66g1Pe.jpg\"\n" +
                "      },\n" +
                "      \"title\": \"Title\",\n" +
                "      \"url\": \"https://google.com/\",\n" +
                "      \"description\": \"Text message. You can use Markdown here. *Italic* **bold** __underline__ ~~strikeout~~ [hyperlink](https://google.com) `code`\",\n" +
                "      \"color\": 15258703,\n" +
                "      \"fields\": [\n" +
                "        {\n" +
                "          \"name\": \"Text\",\n" +
                "          \"value\": \"More text\",\n" +
                "          \"inline\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"Even more text\",\n" +
                "          \"value\": \"Yup\",\n" +
                "          \"inline\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"Use `\\\"inline\\\": true` parameter, if you want to display fields in the same line.\",\n" +
                "          \"value\": \"okay...\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"Thanks!\",\n" +
                "          \"value\": \"You're welcome :wink:\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"thumbnail\": {\n" +
                "        \"url\": \"https://upload.wikimedia.org/wikipedia/commons/3/38/4-Nature-Wallpapers-2014-1_ukaavUI.jpg\"\n" +
                "      },\n" +
                "      \"image\": {\n" +
                "        \"url\": \"https://upload.wikimedia.org/wikipedia/commons/5/5a/A_picture_from_China_every_day_108.jpg\"\n" +
                "      },\n" +
                "      \"footer\": {\n" +
                "        \"text\": \"Woah! So cool! :smirk:\",\n" +
                "        \"icon_url\": \"https://i.imgur.com/fKL31aD.jpg\"\n" +
                "      }\n" +
                "    }\n" +                "  ]\n" +
                "}";

        webhookService.sendPostRequest(url, json);

        // Save the extracted text to a file
        String filePath = "./app/stocks.txt";

        // FileWriterUtil.writeStocksToFile(stockSet, filePath);
    }
}
