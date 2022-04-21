package net.permutated.norecipeadvancements.filters;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;


import java.util.Objects;

@Plugin(name = "UnknownRecipeCategoryFilter", category = Node.CATEGORY, elementType = Filter.ELEMENT_TYPE)
public class UnknownRecipeCategoryFilter extends AbstractFilter {
    @Override
    public Result filter(LogEvent event) {
        Message message = event.getMessage();
        if (Objects.equals(message.getFormat(), "Unknown recipe category: {}/{}")) {
            return Result.DENY;
        } else {
            return Result.NEUTRAL;
        }
    }
}
