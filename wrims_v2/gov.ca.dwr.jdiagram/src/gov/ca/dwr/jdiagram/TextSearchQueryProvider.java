package gov.ca.dwr.jdiagram;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.ui.IWorkingSet;

public class TextSearchQueryProvider extends
		org.eclipse.search.ui.text.TextSearchQueryProvider {

	public TextSearchQueryProvider() {
		super();
	}

	@Override
	public ISearchQuery createQuery(TextSearchInput textSearchInput)
			throws CoreException {
		return getPreferred().createQuery(textSearchInput);
	}

	@Override
	public ISearchQuery createQuery(String textSearchInput) throws CoreException {
		return getPreferred().createQuery(textSearchInput);
	}

	@Override
	public ISearchQuery createQuery(String textSearchInput, IWorkingSet[] ws)
			throws CoreException {
		return getPreferred().createQuery(textSearchInput, ws);
	}

	@Override
	public ISearchQuery createQuery(String textSearchInput, IResource[] resources)
			throws CoreException {
		return getPreferred().createQuery(textSearchInput, resources);
	}

}
