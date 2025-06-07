package pro.shushi.pamirs.eip.api.entity;

import java.io.Serializable;

/**
 * EIP抽象Http请求体
 *
 * @author Adamancy Zhang on 2021-02-05 17:25
 */
public abstract class AbstractEipHttpRequestBody extends AbstractRequestBody implements Serializable {

    private static final long serialVersionUID = -5335697665166611681L;

    private String basePath;

    private String schema;

    private String host;

    private int port;

    private String path;

    private String uri;

    private String userAgent;

    private String contentType;

    private String contentLanguage;

    private String charsetName;

    private String httpMethod;

    private String tags;

    public AbstractEipHttpRequestBody() {
        this.userAgent = "Pamirs Eip/2.2.0 (Language=Java/1.8.0.221; Platform=MAC OS)";
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentLanguage() {
        return contentLanguage;
    }

    public void setContentLanguage(String contentLanguage) {
        this.contentLanguage = contentLanguage;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    protected <NEW extends AbstractEipHttpRequestBody> NEW transferToNewInstance(NEW newBody) {
        newBody.setBasePath(this.getBasePath());
        newBody.setSchema(this.getSchema());
        newBody.setHost(this.getHost());
        newBody.setPort(this.getPort());
        newBody.setPath(this.getPath());
        newBody.setUri(this.getUri());
        newBody.setUserAgent(this.getUserAgent());
        newBody.setContentType(this.getContentType());
        newBody.setContentLanguage(this.getContentLanguage());
        newBody.setCharsetName(this.getCharsetName());
        newBody.setHttpMethod(this.getHttpMethod());
        newBody.setTags(this.getTags());
        return super.transferToNewInstance(newBody);
    }
}
