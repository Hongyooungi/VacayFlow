package com.example.vacayflow.network;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "response", strict = false)
public class PetTourResponse {
    @Element(name = "header", required = false)
    private Header header;

    @Element(name = "body", required = false)
    private Body body;

    public Header getHeader() {
        return header;
    }

    public Body getBody() {
        return body;
    }

    @Root(name = "header", strict = false)
    public static class Header {
        @Element(name = "resultCode", required = false)
        private String resultCode;

        @Element(name = "resultMsg", required = false)
        private String resultMsg;

        public String getResultCode() {
            return resultCode;
        }

        public String getResultMsg() {
            return resultMsg;
        }
    }

    @Root(name = "body", strict = false)
    public static class Body {
        @ElementList(name = "items", required = false)
        private List<Item> items;

        public List<Item> getItems() {
            return items;
        }
    }

    @Root(name = "item", strict = false)
    public static class Item {
        @Element(name = "title", required = false)
        private String title;

        @Element(name = "addr1", required = false)
        private String addr1;

        @Element(name = "firstimage", required = false)
        private String firstImage;

        public String getTitle() {
            return title;
        }

        public String getAddr1() {
            return addr1;
        }

        public String getFirstImage() {
            return firstImage;
        }
    }
}
