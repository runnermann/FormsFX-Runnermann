package type.richtext;

public class LinkSpan {
    private Hyperlink link;
    private int start;
    private int end;

    LinkSpan(Hyperlink link, int start, int end) {
        this.link = link;
        this.start = start;
        this.end = end;
    }

    public Hyperlink getLink() {
        return link;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

}
