package me.liaoheng.github.starth.data;

/**
 * @author liaoheng
 * @version 2016-06-25 14:51
 */
public class Page {
    public enum PageState {
        ONE, REFRESH, MORE;

        public static boolean isRefreshUI(PageState state) {
            return state == ONE || state == REFRESH;
        }

        public static boolean isMoreData(PageState state) {
            return state == ONE || state == MORE;
        }
    }

    private long curPage = 1;

    private String next;
    private long   nextPage;

    private String last;
    private long   lastPage;

    private String first;
    private long   firstPage;

    public Page refresh() {
        return new Page();
    }

    public Page more() {
        curPage = nextPage;
        return this;
    }

    public boolean isLast() {
        return curPage >= lastPage;
    }

    public String getNext() {
        return next;
    }

    public Page setNext(String next) {
        this.next = next;
        return this;
    }

    public long getNextPage() {
        return nextPage;
    }

    public Page setNextPage(long nextPage) {
        this.nextPage = nextPage;
        return this;
    }

    public String getLast() {
        return last;
    }

    public Page setLast(String last) {
        this.last = last;
        return this;
    }

    public long getLastPage() {
        return lastPage;
    }

    public Page setLastPage(long lastPage) {
        this.lastPage = lastPage;
        return this;
    }

    public String getFirst() {
        return first;
    }

    public Page setFirst(String first) {
        this.first = first;
        return this;
    }

    public long getFirstPage() {
        return firstPage;
    }

    public Page setFirstPage(long firstPage) {
        this.firstPage = firstPage;
        return this;
    }

    public long getCurPage() {
        return curPage;
    }

    public Page setCurPage(long curPage) {
        this.curPage = curPage;
        return this;
    }
}
