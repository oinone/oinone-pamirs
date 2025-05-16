package pro.shushi.pamirs.meta.dsl.utils;

import com.google.common.base.Splitter;
import pro.shushi.pamirs.meta.domain.fun.Argument;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_COLON;
import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_COMMA;

/**
 * ArgUtils
 *
 * @author yakir on 2022/01/05 16:05.
 */
public class ArgUtils {

    public static List<String> argArray(String argStr) {
        return Optional.ofNullable(argStr)
                .map(_argStr -> Splitter.on(SEPARATOR_COMMA)
                        .omitEmptyStrings()
                        .trimResults()
                        .splitToList(_argStr))
                .orElse(Collections.emptyList());
    }

    public static List<String> argModel(String argModel) {

        return Optional.ofNullable(argModel)
                .map(_argModel -> Splitter.on(SEPARATOR_COLON)
                        .omitEmptyStrings()
                        .trimResults()
                        .splitToList(_argModel))
                .orElse(Collections.emptyList());
    }

    public static String arg(List<Argument> arguments) {
        return Optional.ofNullable(arguments)
                .map(List::stream)
                .orElse(Stream.empty())
                .map(_argument -> _argument.getName() + SEPARATOR_COLON + _argument.getModel())
                .collect(Collectors.joining(SEPARATOR_COMMA));
    }
}
