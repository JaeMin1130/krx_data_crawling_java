package krx.crawling.domain.webhooks.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class Embed {
    private Author author;
    private String title;
    private String url;
    private String description;
    private int color;
    private List<Field> fields;
    private Thumbnail thumbnail;
    private Image image;
    private Footer footer;

    private static class Author {
        private String name;
        private String url;
        private String icon_url;
    }

    @Getter
    @Builder
    private static class Field {
        private String name;
        private String value;
        private boolean inline;
    }

    private static class Thumbnail {
        private String url;
    }

    private static class Image {
        private String url;
    }

    private static class Footer {
        private String text;
        private String icon_url;
    }
}

/*
 * Embed 예시
 * {
      "author": {
        "name": "Birdie♫",
        "url": "https://www.reddit.com/r/cats/",
        "icon_url": "https://i.imgur.com/R66g1Pe.jpg"
      },
      "title": "Title",
      "url": "https://google.com/",
      "description": "Text message. You can use Markdown here. *Italic* **bold** __underline__ ~~strikeout~~ [hyperlink](https://google.com) `code`",
      "color": 15258703,
      "fields": [
        {
          "name": "Text",
          "value": "More text",
          "inline": true
        },
        {
          "name": "Even more text",
          "value": "Yup",
          "inline": true
        },
        {
          "name": "Use `\"inline\": true` parameter, if you want to display fields in the same line.",
          "value": "okay..."
        },
        {
          "name": "Thanks!",
          "value": "You're welcome :wink:"
        }
      ],
      "thumbnail": {
        "url": "https://upload.wikimedia.org/wikipedia/commons/3/38/4-Nature-Wallpapers-2014-1_ukaavUI.jpg"
      },
      "image": {
        "url": "https://upload.wikimedia.org/wikipedia/commons/5/5a/A_picture_from_China_every_day_108.jpg"
      },
      "footer": {
        "text": "Woah! So cool! :smirk:",
        "icon_url": "https://i.imgur.com/fKL31aD.jpg"
      }
    }
 */