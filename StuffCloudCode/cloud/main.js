
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

Parse.Cloud.define('saveMessage', function(request, response) {
                   var fromUserId = request.params.fromUserId,
                   toUserId = request.params.toUserId,
                   itemId = request.params.itemId,
                   text = request.params.text;
                   Parse.Cloud.useMasterKey();
                   var User = Parse.Object.extend('_User');
                   var fromUser;
                   var query = new Parse.Query(User);
                   query.get(fromUserId, {
                             success: function(user) {
                             fromUser = user;
                             },
                             error: function(object, error) {
                             // The object was not retrieved successfully.
                             // error is a Parse.Error with an error code and description.
                             var jsonObject = {
                             "response": error
                             };
                             
                             response.error(jsonObject)
                             }
                             });
                   
                   var toUser;
                   var query = new Parse.Query(User);
                   query.get(fromUserId, {
                             success: function(user) {
                             toUser = user;
                             },
                             error: function(object, error) {
                             // The object was not retrieved successfully.
                             // error is a Parse.Error with an error code and description.
                             var jsonObject = {
                             "response": error
                             };
                             
                             response.error(jsonObject)
                             }
                             });
                   
                   var item;
                   var query = new Parse.Query(Item);
                   query.get(itemId, {
                             success: function(it) {
                             item = it;
                             },
                             error: function(object, error) {
                             // The object was not retrieved successfully.
                             // error is a Parse.Error with an error code and description.
                             var jsonObject = {
                             "response": error
                             };
                             
                             response.error(jsonObject)
                             }                             
                             });

                   
                                   
                   var Message = Parse.Object.extend('Message'),
                                   message = new Message();
                                   message.set("fromUser",fromUser);
                                   message.set("toUser",toUser);
                                   message.set("item",item);
                                   message.set("text",text);
                   
                   
                   message.save().then(function(message) {
                                       
                                       var jsonObject = {
                                       "messaage": message
                                       };
                                       
                                       response.success(jsonObject);
                                    }, function(error) {
                                       var jsonObject = {
                                       "error": error
                                       };
                                       
                                       response.success(jsonObject);
                                    });
                   });
