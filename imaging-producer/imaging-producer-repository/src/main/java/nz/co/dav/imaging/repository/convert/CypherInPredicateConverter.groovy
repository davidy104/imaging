package nz.co.dav.imaging.repository.convert

import static com.google.common.base.Preconditions.checkNotNull
import groovy.util.logging.Slf4j

import com.google.common.base.Function
import com.google.common.base.Joiner

@Slf4j
class CypherInPredicateConverter implements Function<Set<String>, String> {

	@Override
	String apply(final Set<String> inputSet) {
		checkNotNull(inputSet,"inputSet can not be null")
		List<String> names = []
		inputSet.each{ names << "'${it}'" }
		StringBuilder builder = new StringBuilder("[")
		builder = Joiner.on(",").appendTo(builder, names)
		builder.append("]")
		return builder.toString()
	}
}
