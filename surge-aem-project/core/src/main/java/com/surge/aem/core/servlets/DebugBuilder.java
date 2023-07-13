package com.surge.aem.core.servlets;

import java.io.IOException;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.naming.directory.SearchResult;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.myproject.aem.core.servlets.HashMap;

@Component(service = Servlet.class)

@SlingServletResourceTypes(resourceTypes = "/content/we-retail/us/en", selectors = { "add", "sub",
"surge" }, extensions = { "txt", "json", "xml" })
@SlingServletPaths(value = "bin/demo")
public class DebugBuilder<Hits> extends SlingSafeMethodsServlet {

	@Reference
	QueryBuilder queryBuilder;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse res)
			throws IOException {
		Map<String, String>	predicate = new HashMap<String, String>();
		predicate.put("type", "cq:Page");
		predicate.put("path", "/content/we-retail/us/en");
		predicate.put("orderby.sort", "cq:Page");
		predicate.put("p.limit", "6");


		Query query =	queryBuilder.createQuery(PredicateGroup.create(predicate), 
				req.getResourceResolver().adaptTo(Session.class));

		query.getResult();
		com.day.cq.search.result.SearchResult searchresult = query.getResult();
		java.util.List<Hit> resourceList = searchresult.getHits();
		JsonArrayBuilder jab = Json.createArrayBuilder();
		for(Hit hits:resourceList)
		{
			try {
				JsonObjectBuilder job =Json.createObjectBuilder();
				Resource resource = ((Hit) hits).getResource();
				Resource content = resource.getResourceResolver().getResource(resource.getPath()+"/jcr:content");
				job.add("title",content.getValueMap().get("jcr:title",String.class));
				job.add("path",resource.getPath());
				jab.add(job);
			}

			catch(RepositoryException re){
				re.printStackTrace();
			}
		}
		res.getWriter().write(jab.build().toString());
	}
}