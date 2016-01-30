from flask import Flask
from flask import render_template, request, redirect, session, url_for, escape, make_response, flash, abort

app = Flask(__name__)
app.secret_key = "bnNoqxXSgzoXSZjb8mrMp5L0L4mJ4o8nRzn"

@app.route('/authenticate')
def authenticate():
	return "authenticate here"

@app.route('/register')
def register():
	return "register here"

@app.route('/error')
def error():
	abort(401)


if __name__ == '__main__':
	#host='0.0.0.0' only with debug disabled
	app.run(port=8080, debug=True) 