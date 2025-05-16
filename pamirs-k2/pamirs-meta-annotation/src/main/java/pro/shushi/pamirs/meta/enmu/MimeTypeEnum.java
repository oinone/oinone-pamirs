package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * MIME类型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.Mime", displayName = "MIME类型")
public enum MimeTypeEnum implements IEnum<String> {

    aac("audio/aac", "AAC audio", "AAC audio"),
    abw("application/x-abiword", "AbiWord document", "AbiWord document"),
    arc("application/x-freearc", "Archive document (multiple files embedded)", "Archive document (multiple files embedded)"),
    avi("video/x-msvideo", "AVI: Audio Video Interleave", "AVI: Audio Video Interleave"),
    azw("application/vnd.amazon.ebook", "Amazon Kindle eBook format", "Amazon Kindle eBook format"),
    bin("application/octet-stream", "Any kind of binary data", "Any kind of binary data"),
    bmp("image/bmp", "Windows OS/2 Bitmap Graphics", "Windows OS/2 Bitmap Graphics"),
    bz("application/x-bzip", "BZip archive", "BZip archive"),
    bz2("application/x-bzip2", "BZip2 archive", "BZip2 archive"),
    csh("application/x-csh", "C-Shell script", "C-Shell script"),
    css("text/css", "Cascading Style Sheets (CSS)", "Cascading Style Sheets (CSS)"),
    csv("text/csv", "Comma-separated values (CSV)", "Comma-separated values (CSV)"),
    doc("application/msword", "Microsoft Word", "Microsoft Word"),
    docx("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Microsoft Word (OpenXML)", "Microsoft Word (OpenXML)"),
    eot("application/vnd.ms-fontobject", "MS Embedded OpenType fonts", "MS Embedded OpenType fonts"),
    epub("application/epub+zip", "Electronic publication (EPUB)", "Electronic publication (EPUB)"),
    gif("image/gif", "Graphics Interchange Format (GIF)", "Graphics Interchange Format (GIF)"),
    //    htm("text/html", "HyperText Markup Language (HTML)", "HyperText Markup Language (HTML)"),
    html("text/html", "HyperText Markup Language (HTML)", "HyperText Markup Language (HTML)"),
    ico("image/vnd.microsoft.icon", "Icon format", "Icon format"),
    ics("text/calendar", "iCalendar format", "iCalendar format"),
    jar("application/java-archive", "Java Archive (JAR)", "Java Archive (JAR)"),
    jpeg("image/jpeg", "JPEG images", "JPEG images"),
    //    jpg("image/jpeg", "JPEG images", "JPEG images"),
    js("text/javascript", "JavaScript", "JavaScript"),
    json("application/json", "JSON format", "JSON format"),
    jsonld("application/ld+json", "JSON-LD format", "JSON-LD format"),
    mid("audio/midi", "Musical Instrument Digital Interface (MIDI)", "Musical Instrument Digital Interface (MIDI)"),
    midi("audio/x-midi", "Musical Instrument Digital Interface (MIDI)", "Musical Instrument Digital Interface (MIDI)"),
    mjs("text/javascript", "JavaScript module", "JavaScript module"),
    mp3("audio/mpeg", "MP3 audio", "MP3 audio"),
    mpeg("video/mpeg", "MPEG Video", "MPEG Video"),
    mpkg("application/vnd.apple.installer+xml", "Apple Installer Package", "Apple Installer Package"),
    odp("application/vnd.oasis.opendocument.presentation", "OpenDocument presentation document", "OpenDocument presentation document"),
    ods("application/vnd.oasis.opendocument.spreadsheet", "OpenDocument spreadsheet document", "OpenDocument spreadsheet document"),
    odt("application/vnd.oasis.opendocument.text", "OpenDocument text document", "OpenDocument text document"),
    oga("audio/ogg", "OGG audio", "OGG audio"),
    ogv("video/ogg", "OGG video", "OGG video"),
    ogx("application/ogg", "OGG", "OGG"),
    otf("font/otf", "OpenType font", "OpenType font"),
    png("image/png", "Portable Network Graphics", "Portable Network Graphics"),
    pdf("application/pdf", "Adobe Portable Document Format (PDF)", "Adobe Portable Document Format (PDF)"),
    ppt("application/vnd.ms-powerpoint", "Microsoft PowerPoint", "Microsoft PowerPoint"),
    pptx("application/vnd.openxmlformats-officedocument.presentationml.presentation", "Microsoft PowerPoint (OpenXML)", "Microsoft PowerPoint (OpenXML)"),
    rar("application/x-rar-compressed", "RAR archive", "RAR archive"),
    rtf("application/rtf", "Rich Text Format (RTF)", "Rich Text Format (RTF)"),
    sh("application/x-sh", "Bourne shell script", "Bourne shell script"),
    svg("image/svg+xml", "Scalable Vector Graphics (SVG)", "Scalable Vector Graphics (SVG)"),
    swf("application/x-shockwave-flash", "Small web format (SWF) or Adobe Flash document", "Small web format (SWF) or Adobe Flash document"),
    tar("application/x-tar", "Tape Archive (TAR)", "Tape Archive (TAR)"),
    //    tif("image/tiff", "Tagged Image File Format (TIFF)", "Tagged Image File Format (TIFF)"),
    tiff("image/tiff", "Tagged Image File Format (TIFF)", "Tagged Image File Format (TIFF)"),
    ttf("font/ttf", "TrueType Font", "TrueType Font"),
    txt("text/plain", "Text, (generally ASCII or ISO 8859-n)", "Text, (generally ASCII or ISO 8859-n)"),
    vsd("application/vnd.visio", "Microsoft Visio", "Microsoft Visio"),
    wav("audio/wav", "Waveform Audio Format", "Waveform Audio Format"),
    weba("audio/webm", "WEBM audio", "WEBM audio"),
    webm("video/webm", "WEBM video", "WEBM video"),
    webp("image/webp", "WEBP image", "WEBP image"),
    woff("font/woff", "Web Open Font Format (WOFF)", "Web Open Font Format (WOFF)"),
    woff2("font/woff2", "Web Open Font Format (WOFF)", "Web Open Font Format (WOFF)"),
    xhtml("application/xhtml+xml", "XHTML", "XHTML"),
    xls("application/vnd.ms-excel", "Microsoft Excel", "Microsoft Excel"),
    xlsx("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Microsoft Excel (OpenXML)", "Microsoft Excel (OpenXML)"),
    xml("text/xml", "XML", "XML"),
    xul("application/vnd.mozilla.xul+xml", "XUL", "XUL"),
    zip("application/zip", "ZIP archive", "ZIP archive"),
    threegp("video/3gpp", "3GPP audio/video container", "3GPP audio/video container"),
    threeg2("video/3gpp2", "3GPP2 audio/video container", "3GPP2 audio/video container"),
    sevenz("application/x-7z-compressed", "7-zip archive", "7-zip archive"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    MimeTypeEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }

}