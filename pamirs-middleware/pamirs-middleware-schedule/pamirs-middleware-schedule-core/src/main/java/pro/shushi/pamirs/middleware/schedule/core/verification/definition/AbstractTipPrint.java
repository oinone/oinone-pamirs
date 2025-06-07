package pro.shushi.pamirs.middleware.schedule.core.verification.definition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.ITipPrint;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 16:55
 */
public abstract class AbstractTipPrint implements ITipPrint {

    protected static final Logger logger = LoggerFactory.getLogger(ITipPrint.class);

    private int maxLineSize = -1;

    private final List<String> lines = new ArrayList<>();

    @Override
    public void appendLine(String line) {
        lines.add(line);
        int length = line.length();
        if (maxLineSize < length) {
            maxLineSize = length;
        }
    }

    @Override
    public String getTipText() {
        int leftMargin = getLeftMargin();
        int rightMargin = getRightMargin();
        int finalLineSize = leftMargin + maxLineSize + rightMargin + 2;
        StringBuilder sb = new StringBuilder();
        printTopBorder(sb, finalLineSize);
        int topMargin = getTopMargin();
        for (int i = 0; i < topMargin; i++) {
            printLine(sb, "", leftMargin, rightMargin);
        }
        for (String line : lines) {
            printLine(sb, line, leftMargin, rightMargin);
        }
        int bottomMargin = getBottomMargin();
        for (int i = 0; i < bottomMargin; i++) {
            printLine(sb, "", leftMargin, rightMargin);
        }
        printBottomBorder(sb, finalLineSize);
        return sb.toString();
    }

    @Override
    public void print() {
        print(getTipText());
    }

    protected void printTopBorder(StringBuilder sb, int finalLineSize) {
        char topBorder = getTopBorder();
        for (int i = 0; i < finalLineSize; i++) {
            sb.append(topBorder);
        }
        sb.append("\r\n");
    }

    protected void printBottomBorder(StringBuilder sb, int finalLineSize) {
        char bottomBorder = getBottomBorder();
        for (int i = 0; i < finalLineSize; i++) {
            sb.append(bottomBorder);
        }
        sb.append("\r\n");
    }

    protected void printLine(StringBuilder sb, String line, int leftMargin, int rightMargin) {
        sb.append(getLeftBorder());
        for (int i = 0; i < leftMargin + 1; i++) {
            sb.append(" ");
        }
        sb.append(line);
        rightMargin = maxLineSize - line.length() + rightMargin;
        for (int i = 0; i < rightMargin; i++) {
            sb.append(" ");
        }
        sb.append(getRightBorder());
    }

    /**
     * call print text.
     *
     * @param text final tip text
     */
    protected abstract void print(String text);
}
