from flask import Flask, request, send_from_directory

# set the project root directory as the static folder, you can set others.
app = Flask(__name__, static_url_path='/static')

@app.route('/')
def home():
	return app.send_static_file('index.html')

if __name__ == "__main__":
    app.run()