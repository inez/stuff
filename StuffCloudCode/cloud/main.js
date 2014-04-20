
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

var _ = require("underscore");
Parse.Cloud.beforeSave("Item", function(request, response) {
	var item = request.object;
	var toLowerCase = function(w) { return w.toLowerCase(); };
	item.set("searchable", toLowerCase(item.get("name")+" "+item.get("description")));
    response.success();
 	
 	/*
    var nameWords = item.get("name").split(/\b/);
    nameWords = _.map(nameWords, toLowerCase);

    var descriptionWords = item.get("description").split(/\b/);
    descriptionWords = _.map(descriptionWords, toLowerCase);

	var stopWords = ["the", "in", "and"]

	var words = nameWords.concat(descriptionWords);
    words = _.filter(words, function(w) {
    	return w.match(/^\w+$/) && ! _.contains(stopWords, w);
    } );

	item.set("words", words);
	*/
});

/*
Parse.Cloud.afterSave("Item", function(request) {
  query = new Parse.Query("Item");
  query.get(request.object.get("item").id, {
    success: function(item) {
      item.set("words", ["a","b","c"]);
      item.save();
    }
  });
});
*/