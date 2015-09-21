AJS.$( document ).ready(function() {
	var group = AJS.$('ul.aui-nav');
	
	//hide users
	AJS.$('#add-key-button').attr('href',AJS.contextPath()+'/plugins/servlet/account/enterprisekey');
	//hide repos
	AJS.$('li [data-web-item-key="com.atlassian.stash.ssh-plugin:repository-access-keys-tab"]').parent().hide();
	//hide projects
	AJS.$('li [data-web-item-key="com.atlassian.stash.ssh-plugin:project-access-keys-tab"]').parent().hide();
});