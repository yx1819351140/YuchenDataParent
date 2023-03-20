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
    private String mediaSector;

    //板块URL, 用来根据URL确定具体板块
    private String mediaSectorUrl;
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

    public String getMediaSector() {
        return mediaSector;
    }

    public void setMediaSector(String mediaSector) {
        this.mediaSector = mediaSector;
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

    public String getMediaSectorUrl() {
        return mediaSectorUrl;
    }

    public void setMediaSectorUrl(String mediaSectorUrl) {
        this.mediaSectorUrl = mediaSectorUrl;
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

        public Builder mediaName(String mediaName) {
            mediaInfo.setMediaName(mediaName);
            return this;
        }

        public Builder mediaSector(String mediaSector) {
            mediaInfo.setMediaSector(mediaSector);
            return this;
        }

        public Builder mediaSectorUrl(String mediaSectorUrl) {
            mediaInfo.setMediaSectorUrl(mediaSectorUrl);
            return this;
        }

        public Builder mediaNameZh(String mediaNameZh) {
            mediaInfo.setMediaNameZh(mediaNameZh);
            return this;
        }

        public Builder mediaLang(String mediaLang) {
            mediaInfo.setMediaLang(mediaLang);
            return this;
        }

        public Builder countryId(String countryId) {
            mediaInfo.setCountryId(countryId);
            return this;
        }

        public Builder countryCode(String countryCode) {
            mediaInfo.setCountryCode(countryCode);
            return this;
        }

        public Builder countryName(String countryName) {
            mediaInfo.setCountryName(countryName);
            return this;
        }

        public Builder countryNameZh(String countryNameZh) {
            mediaInfo.setCountryNameZh(countryNameZh);
            return this;
        }

        public MediaInfo build() {
            return mediaInfo;
        }
    }
}
