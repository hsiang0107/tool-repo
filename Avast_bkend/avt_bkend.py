__author__ = 'sean.huang'
import sqlite3
import configuration as config
from flask import Flask, url_for, render_template, request, g, redirect, jsonify
from contextlib import closing

web_srv = Flask(__name__)
web_srv.config.from_object(config)

def connect_db():
    return sqlite3.connect(web_srv.config['DATABASE'])

def init_db():
    with closing(connect_db()) as db:
        with web_srv.open_resource('schema.sql', mode='r') as f:
            db.cursor().executescript(f.read())
        db.commit()

@web_srv.before_request
def before_request():
    g.db = connect_db()

@web_srv.teardown_request
def teardown_request(exception):
    db = getattr(g, 'db', None)
    if db is not None:
        db.close()

@web_srv.route('/', methods=['GET'])
def show_entries():
    cur = g.db.execute('select id, title from entries order by id desc')
    entries = [dict(id=row[0], title=row[1]) for row in cur.fetchall()]
    # return jsonify(id=entries[0]['id'], title=entries[0]['title'])
    return jsonify(items=entries)

@web_srv.route('/add', methods=['POST'])
def add_entry():
    data = request.get_json(force=True)
    g.db.execute('insert into entries (title) values (?)', [data['title']])
    g.db.commit()
    return redirect(url_for('show_entries'))

@web_srv.route('/delete/<int:delete_id>', methods=['DELETE'])
def delete_entry(delete_id):
    g.db.execute('delete from entries where id = (?)', [delete_id])
    g.db.commit()
    return redirect(url_for('show_entries'))

if __name__ == "__main__":
    web_srv.run(host='127.0.0.1')