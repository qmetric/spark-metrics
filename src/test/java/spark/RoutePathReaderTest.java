package spark;

import org.junit.Test;

import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyLetterString;
import static org.fest.assertions.Assertions.assertThat;

public class RoutePathReaderTest
{

    final String path = anyLetterString();

    final Route route = new Route(path)
    {
        @Override public Object handle(final Request request, final Response response)
        {
            return null;
        }
    };

    @Test
    public void reads_PATH_of_Route_even_if_it_has_package_scope()
    {
        assertThat(RoutePathReader.path(route)).isEqualTo(path);
    }
}
