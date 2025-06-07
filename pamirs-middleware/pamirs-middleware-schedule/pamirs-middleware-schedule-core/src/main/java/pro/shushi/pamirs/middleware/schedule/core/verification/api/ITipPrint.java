package pro.shushi.pamirs.middleware.schedule.core.verification.api;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 16:58
 */
public interface ITipPrint {

    /**
     * get top border
     *
     * @return top border char
     */
    default char getTopBorder() {
        return '*';
    }

    /**
     * get right border
     *
     * @return right border char
     */
    default char getRightBorder() {
        return '*';
    }

    /**
     * get bottom border
     *
     * @return bottom border char
     */
    default char getBottomBorder() {
        return '*';
    }

    /**
     * get left border
     *
     * @return left border char
     */
    default char getLeftBorder() {
        return '*';
    }

    /**
     * get top margin char number
     *
     * @return top margin char number
     */
    default int getTopMargin() {
        return 2;
    }

    /**
     * get right margin char number
     *
     * @return right margin char number
     */
    default int getRightMargin() {
        return 2;
    }

    /**
     * get bottom margin char number
     *
     * @return bottom margin char number
     */
    default int getBottomMargin() {
        return 2;
    }

    /**
     * get left margin char number
     *
     * @return left margin char number
     */
    default int getLeftMargin() {
        return 2;
    }

    /**
     * append line to {@link java.util.List}
     *
     * @param line text
     */
    void appendLine(String line);

    /**
     * generator tip text.
     *
     * @return final tip text.
     */
    String getTipText();

    /**
     * call print
     */
    void print();
}
