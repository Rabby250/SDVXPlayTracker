package com.rabby250.sdvxtracker.content;

import android.text.TextUtils;
import com.rabby250.sdvxtracker.utility.Html;
import com.rabby250.sdvxtracker.utility.Utilities;
import com.rabby250.sdvxtracker.utility.UuidUtilities;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

public class MusicList {

    private static final String BASE_URL
            = SdvxPortal.RANKING_URL + "/index.html";
    private static final String URL_DATA_PAGE = "page";
    private static final String URL_DATA_SORT = "sort";

    private static final String ELEMENT_SONG
            = Html.TAG_DIV + ".music";
    private static final String ELEMENT_SONG_TITLE
            = Html.TAG_DIV + ".music_name";
    private static final String ELEMENT_PAGE_LIST
            = Html.TAG_DIV + ".play_score_ranking_pager";
    private static final String ELEMENT_PAGE_INDEX
            = Html.TAG_SPAN + ".page_num";

    public static int countPages(final int sortIndex) {
        final Document currentPage;
        try {
            currentPage = Jsoup.connect(BASE_URL)
                    .data(URL_DATA_SORT, Integer.toString(sortIndex))
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        final Element pageList = currentPage
                .select(ELEMENT_PAGE_LIST).first();
        if (pageList == null) {
            return 0;
        }
        // Check page # of the current page
        final int currentIndex = Utilities.extractNumber(
                pageList.ownText(), 0);
        final Element lastPageSpan = pageList
                .select(ELEMENT_PAGE_INDEX).last();
        // No links to other pages -> current page is the only page
        if (lastPageSpan == null) {
            return currentIndex == 1 ? 1 : 0;
        }
        final int lastIndex = Integer.parseInt(lastPageSpan.ownText());
        // Page # of the last link was smaller than current page
        // -> we're on the last page, return current page # instead
        return lastIndex >= currentIndex ? lastIndex : currentIndex;
    }

    public static HashSet<MusicData> parseMusicList(
            final int sortIndex, final int pageIndex) {
        final Document listPage;
        try {
            listPage = Jsoup.connect(BASE_URL)
                    .data(URL_DATA_PAGE, Integer.toString(pageIndex))
                    .data(URL_DATA_SORT, Integer.toString(sortIndex))
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
        final Elements listElements = listPage.select(ELEMENT_SONG);
        final HashSet<MusicData> musicList
                = new HashSet<>(listElements.size());

        for (Element el : listElements) {
            // Look for hyperlinks to detailed rankings
            // and parse music IDs from the links
            final Element songLink = el.getElementsByAttribute(
                    Html.ATTR_HREF).first();
            if (songLink == null) {
                continue;
            }
            final UUID musicId;
            try {
                final String url = songLink.attr(Html.ATTR_HREF);
                final int start = url.indexOf("id=") + 3;
                int end = url.indexOf('&', start);
                if (end < 0) {
                    end = url.length();
                }
                musicId = UuidUtilities.decode(
                        url.substring(start, end));
                if (musicId == null) {
                    continue;
                }
            } catch (StringIndexOutOfBoundsException e) {
                continue;
            }

            // Fetch titles
            final Element titleSection
                    = el.select(ELEMENT_SONG_TITLE).first();
            if (titleSection == null) {
                continue;
            }
            final String title = titleSection.ownText();
            if (TextUtils.isEmpty(title)) {
                continue;
            }

            musicList.add(new MusicData(musicId, title));
        }

        return musicList;
    }
}
