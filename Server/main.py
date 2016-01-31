from flask import Flask
from flask import render_template, request, redirect, session, url_for, escape, make_response, flash, abort
import hashlib
from models import User
import time
import json

app = Flask(__name__)
app.secret_key = "bnNoqxXSgzfdajkdsafadsoXSZjb8mrMp5L0L4mJ4o8nRzn"

def hash(data):
	return hashlib.sha256(str(data)+"salt2j4Eo").hexdigest()

def hash_strong(data):
	h = data
	for i in range(100):
		h = hash(h)
	return h

def update_db():
	users = User.query().fetch()
	t = str(time.time()//300) # 5 min

	if(len(users) == 0):
		return

	if(hash(users[0].original_hash + t) == users[0].timed_hash):
		return # Already updated this time interval

	for user in users:
		user.timed_hash = hash(user.original_hash + t)
		user.put()


@app.route('/')
def index():
	return render_template('index.html')

@app.route('/authenticate', methods=['GET', 'POST'])
def authenticate():
	if(request.method == 'GET'):
		update_db() # todo: remove?
		flash("Authentication started - you've got 5 minutes")
		return redirect(url_for('index'))

	# POST
	h = request.form['hash']
	if(h is None):
		abort(400)

	matches = User.query(User.timed_hash==h).fetch()
	if(len(matches) > 0):
		return json.dumps({"username":matches[0].username, "id":str(matches[0].key.pairs()[0][1])})
	else:
		return abort(401)

@app.route('/register', methods=['POST'])
def register():
	h = hash_strong(request.form['hash'])
	username = request.form['username']
	if(h is None or username is None):
		abort(400)
	print(username)
	u = User(username=username, original_hash=h)
	u.put()
	update_db()
	return "OK"

@app.route('/error')
def error():
	abort(400)

@app.route('/debug')
def debug():
	s = ""
	for i in User.query().fetch():
		s += str(i) + "<br><br>\n"
	return s

@app.after_request
def after_request(response):
	response.headers.add('Access-Control-Allow-Origin', '*')
	return response

if __name__ == '__main__':
	#host='0.0.0.0' only with debug disabled
	app.run(port=8080, debug=True) 


