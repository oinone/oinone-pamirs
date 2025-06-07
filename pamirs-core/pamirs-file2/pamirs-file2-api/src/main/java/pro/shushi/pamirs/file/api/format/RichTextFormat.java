package pro.shushi.pamirs.file.api.format;

import pro.shushi.pamirs.file.api.model.ExcelTypefaceDefinition;

public class RichTextFormat {

    public RichTextFormat(int startIndex, int endIndex, ExcelTypefaceDefinition typeface) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.typeface = typeface;
    }

    private final int startIndex;

    private final int endIndex;

    private final ExcelTypefaceDefinition typeface;

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public ExcelTypefaceDefinition getTypeface() {
        return typeface;
    }
}
