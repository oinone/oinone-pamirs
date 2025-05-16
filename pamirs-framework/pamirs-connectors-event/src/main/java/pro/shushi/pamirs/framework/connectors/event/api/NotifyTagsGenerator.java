package pro.shushi.pamirs.framework.connectors.event.api;

@FunctionalInterface
public interface NotifyTagsGenerator {

    String tagsGenerator(Object /* msg payload */ event);
}
