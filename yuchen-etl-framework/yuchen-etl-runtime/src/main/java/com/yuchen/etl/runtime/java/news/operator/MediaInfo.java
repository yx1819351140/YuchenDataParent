package com.yuchen.etl.runtime.java.news.operator;

/**
 * @Author: xiaozhennan
 * @Date: 2023/2/27 9:40
 * @Package: com.yuchen.etl.runtime.java.news.operator
 * @ClassName: MediaInfo
 * @Description:
 **/
public class MediaInfo {
    private String domain;
    private String mediaName;
    private String mediaNameZh;
    private String mediaLang;
    private String countryId;
    private String countryCode;
    private String countryName;
    private String countryNameZh;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getMediaNameZh() {
        return mediaNameZh;
    }

    public void setMediaNameZh(String mediaNameZh) {
        this.mediaNameZh = mediaNameZh;
    }

    public String getMediaLang() {
        return mediaLang;
    }

    public void setMediaLang(String mediaLang) {
        this.mediaLang = mediaLang;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryNameZh() {
        return countryNameZh;
    }

    public void setCountryNameZh(String countryNameZh) {
        this.countryNameZh = countryNameZh;
    }

    public static final class Builder {
        private MediaInfo mediaInfo;

        private Builder() {
            mediaInfo = new MediaInfo();
        }

        public static Builder aMediaInfo() {
            return new Builder();
        }

        public Builder domain(String domain) {
            mediaInfo.setDomain(domain);
            return this;
        }

        public Builder mediaName(String media_name) {
            mediaInfo.setMediaName(media_name);
            return this;
        }

        public Builder media_name_zh(String media_name_zh) {
            mediaInfo.setMediaNameZh(media_name_zh);
            return this;
        }

        public Builder media_lang(String media_lang) {
            mediaInfo.setMediaLang(media_lang);
            return this;
        }

        public Builder country_id(String country_id) {
            mediaInfo.setCountryId(country_id);
            return this;
        }

        public Builder country_code(String country_code) {
            mediaInfo.setCountryCode(country_code);
            return this;
        }

        public Builder country_name(String country_name) {
            mediaInfo.setCountryName(country_name);
            return this;
        }

        public Builder country_name_zh(String country_name_zh) {
            mediaInfo.setCountryNameZh(country_name_zh);
            return this;
        }

        public MediaInfo build() {
            return mediaInfo;
        }
    }
}
