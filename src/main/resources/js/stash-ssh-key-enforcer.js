AJS.$( document ).ready(function() {
	var group = AJS.$('ul.aui-nav');
	
	//hide users
	AJS.$('li [data-web-item-key="com.atlassian.stash.ssh-plugin:account-ssh-keys-tab"]').parent().hide();
	//hide repos
	AJS.$('li [data-web-item-key="com.atlassian.stash.ssh-plugin:repository-access-keys-tab"]').parent().hide();
	//hide projects
	AJS.$('li [data-web-item-key="com.atlassian.stash.ssh-plugin:project-access-keys-tab"]').parent().hide();
});