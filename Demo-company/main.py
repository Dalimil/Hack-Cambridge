"""`main` is the top level module for your Flask application."""

# Import the Flask Framework
from flask import Flask, request, render_template
app = Flask(__name__)
# Note: We don't need to call run() since our application is embedded within
# the App Engine WSGI application server.


@app.route('/')
def hello():
    """Return a friendly HTTP greeting."""
    return render_template('index.html')


