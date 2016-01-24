package fasttags;

import groovy.lang.Closure;
import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;
import play.templates.JavaExtensions;
import play.templates.TagContext;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jeff
 */
public class CustomFastTags extends FastTags {

    public static void _option(Map<?, ?> args, Closure body, PrintWriter out,
                               ExecutableTemplate template, int fromLine) {

        Object value = args.get("arg");

        TagContext context = TagContext.parent("select-multiple");
        boolean selected = false;
        if (context!=null && context.data!=null) {
            Object selection = context.data.get("selected");

            if (selection instanceof List) {
                List<Object> selectionList = (List<Object>) selection;

                selected = selection != null
                        && value != null
                        && selectionList != null
                        && selectionList.contains(value);
            }
        } else {
            context = TagContext.parent("select");
            if (context!=null && context.data!=null) {
                Object selection = context.data.get("selected");
                selected = value!=null && selection!=null && selection.equals(value);
            }
        }

        out.print("<option value=\"" + (value == null ? "" : value) + "\" "
                + (selected ? "selected=\"selected\"" : "")
                + "" + serialize(args, "selected", "value") + ">");
        out.println(JavaExtensions.toString(body));
        out.print("</option>");
    }

}