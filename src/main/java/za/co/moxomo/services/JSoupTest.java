package za.co.moxomo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class JSoupTest {

    private static final Logger logger = LoggerFactory.getLogger(JSoupTest.class);

    private static final String ENDPOINT = "https://www.discovery.co.za/portal/individual/discovery-career-search/search.do";
    private static final  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    public static void main(String[] args) throws Exception {

    }
}