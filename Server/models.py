from google.appengine.ext import ndb

class User(ndb.Model):
	username = ndb.StringProperty()
	original_hash = ndb.StringProperty(required = True)
	timed_hash = ndb.StringProperty(indexed=True)
		