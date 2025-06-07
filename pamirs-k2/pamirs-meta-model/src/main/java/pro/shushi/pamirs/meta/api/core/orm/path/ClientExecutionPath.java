package pro.shushi.pamirs.meta.api.core.orm.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import static java.lang.String.format;
import static pro.shushi.pamirs.meta.api.core.orm.path.Assert.assertNotNull;
import static pro.shushi.pamirs.meta.api.core.orm.path.Assert.assertTrue;


/**
 * As a graphql query is executed, each field forms a hierarchical path from parent field to child field and this
 * class represents that path as a series of segments.
 */
public class ClientExecutionPath {
    private static final ClientExecutionPath ROOT_PATH = new ClientExecutionPath();

    /**
     * All paths start from here
     *
     * @return the root path
     */
    public static ClientExecutionPath rootPath() {
        return ROOT_PATH;
    }

    private final ClientExecutionPath parent;
    private final PathSegment segment;
    private final List<Object> pathList;

    private ClientExecutionPath() {
        parent = null;
        segment = null;
        pathList = toListImpl();
    }

    private ClientExecutionPath(ClientExecutionPath parent, PathSegment segment) {
        this.parent = assertNotNull(parent, "Must provide a parent path");
        this.segment = assertNotNull(segment, "Must provide a sub path");
        pathList = toListImpl();
    }

    public int getLevel() {
        int counter = 0;
        ClientExecutionPath currentPath = this;
        while (currentPath != null) {
            if (currentPath.segment instanceof StringPathSegment) {
                counter++;
            }
            currentPath = currentPath.parent;
        }
        return counter;
    }

    public ClientExecutionPath getPathWithoutListEnd() {
        if (ROOT_PATH.equals(this)) {
            return ROOT_PATH;
        }
        if (segment instanceof StringPathSegment) {
            return this;
        }
        return parent;
    }

    public String getSegmentName() {
        if (segment instanceof StringPathSegment) {
            return ((StringPathSegment) segment).getValue();
        } else {
            if (parent == null) {
                return null;
            }
            return ((StringPathSegment) parent.segment).getValue();
        }
    }

    /**
     * Parses an execution path from the provided path string in the format /segment1/segment2[index]/segmentN
     *
     * @param pathString the path string
     * @return a parsed execution path
     */
    public static ClientExecutionPath parse(String pathString) {
        pathString = pathString == null ? "" : pathString;
        pathString = pathString.trim();
        StringTokenizer st = new StringTokenizer(pathString, "/[]", true);
        ClientExecutionPath path = ClientExecutionPath.rootPath();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if ("/".equals(token)) {
                assertTrue(st.hasMoreTokens(), mkErrMsg(), pathString);
                path = path.segment(st.nextToken());
            } else if ("[".equals(token)) {
                assertTrue(st.countTokens() >= 2, mkErrMsg(), pathString);
                path = path.segment(Integer.parseInt(st.nextToken()));
                String closingBrace = st.nextToken();
                assertTrue(closingBrace.equals("]"), mkErrMsg(), pathString);
            } else {
                throw new RuntimeException(format(mkErrMsg(), pathString));
            }
        }
        return path;
    }

    /**
     * This will create an execution path from the list of objects
     *
     * @param objects the path objects
     * @return a new execution path
     */
    public static ClientExecutionPath fromList(List<?> objects) {
        assertNotNull(objects);
        ClientExecutionPath path = ClientExecutionPath.rootPath();
        for (Object object : objects) {
            if (object instanceof Number) {
                path = path.segment(((Number) object).intValue());
            } else {
                path = path.segment(String.valueOf(object));
            }
        }
        return path;
    }

    private static String mkErrMsg() {
        return "Invalid path string : '%s'";
    }

    /**
     * Takes the current path and adds a new segment to it, returning a new path
     *
     * @param segment the string path segment to add
     * @return a new path containing that segment
     */
    public ClientExecutionPath segment(String segment) {
        return new ClientExecutionPath(this, new StringPathSegment(segment));
    }

    /**
     * Takes the current path and adds a new segment to it, returning a new path
     *
     * @param segment the int path segment to add
     * @return a new path containing that segment
     */
    public ClientExecutionPath segment(int segment) {
        return new ClientExecutionPath(this, new IntPathSegment(segment));
    }

    /**
     * Drops the last segment off the path
     *
     * @return a new path with the last segment dropped off
     */
    public ClientExecutionPath dropSegment() {
        if (this == rootPath()) {
            return null;
        }
        return this.parent;
    }

    /**
     * Replaces the last segment on the path eg ExecutionPath.parse("/a/b[1]").replaceSegment(9)
     * equals "/a/b[9]"
     *
     * @param segment the integer segment to use
     * @return a new path with the last segment replaced
     */
    public ClientExecutionPath replaceSegment(int segment) {
        assertTrue(!ROOT_PATH.equals(this), "You MUST not call this with the root path");

        List<Object> objects = this.toList();
        objects.set(objects.size() - 1, new IntPathSegment(segment).getValue());
        return fromList(objects);
    }

    /**
     * Replaces the last segment on the path eg ExecutionPath.parse("/a/b[1]").replaceSegment("x")
     * equals "/a/b/x"
     *
     * @param segment the string segment to use
     * @return a new path with the last segment replaced
     */
    public ClientExecutionPath replaceSegment(String segment) {
        assertTrue(!ROOT_PATH.equals(this), "You MUST not call this with the root path");

        List<Object> objects = this.toList();
        objects.set(objects.size() - 1, new StringPathSegment(segment).getValue());
        return fromList(objects);
    }


    /**
     * @return true if the end of the path has a list style segment eg 'a/b[2]'
     */
    public boolean isListSegment() {
        return segment instanceof IntPathSegment;
    }

    /**
     * @return true if the end of the path has a named style segment eg 'a/b[2]/c'
     */
    public boolean isNamedSegment() {
        return segment instanceof StringPathSegment;
    }

    /**
     * @return true if the path is the {@link #rootPath()}
     */
    public boolean isRootPath() {
        return this == ROOT_PATH;
    }

    /**
     * Appends the provided path to the current one
     *
     * @param path the path to append
     * @return a new path
     */
    public ClientExecutionPath append(ClientExecutionPath path) {
        List<Object> objects = this.toList();
        objects.addAll(assertNotNull(path).toList());
        return fromList(objects);
    }


    public ClientExecutionPath sibling(String siblingField) {
        assertTrue(!ROOT_PATH.equals(this), "You MUST not call this with the root path");
        return new ClientExecutionPath(this.parent, new StringPathSegment(siblingField));
    }

    /**
     * @return converts the path into a list of segments
     */
    public List<Object> toList() {
        return new ArrayList<>(pathList);
    }

    private List<Object> toListImpl() {
        if (parent == null) {
            return Collections.emptyList();
        }
        List<Object> list = new ArrayList<>();
        ClientExecutionPath p = this;
        while (p.segment != null) {
            list.add(p.segment.getValue());
            p = p.parent;
        }
        Collections.reverse(list);
        return list;
    }


    /**
     * @return the path as a string which represents the call hierarchy
     */
    @Override
    public String toString() {
        if (parent == null) {
            return "";
        }

        if (ROOT_PATH.equals(parent)) {
            return segment.toString();
        }

        return parent.toString() + segment.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientExecutionPath that = (ClientExecutionPath) o;

        return pathList.equals(that.pathList);
    }

    @Override
    public int hashCode() {
        return pathList.hashCode();
    }


    private interface PathSegment<T> {
        T getValue();
    }

    private static class StringPathSegment implements PathSegment<String> {
        private final String value;

        StringPathSegment(String value) {
            assertTrue(value != null && !value.isEmpty(), "empty path component");
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return '/' + value;
        }
    }

    private static class IntPathSegment implements PathSegment<Integer> {
        private final int value;

        IntPathSegment(int value) {
            this.value = value;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "[" + value + ']';
        }
    }

}
