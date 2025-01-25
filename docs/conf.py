import os
import sys

sys.path.insert(0, os.path.abspath('..'))

project = 'Ecliptic Seasons'
author = 'jianzoushihu'
version = '1.0'
release = '1.0.0'

extensions = [
    'sphinx.ext.autodoc',
    'sphinx.ext.viewcode',
    'sphinx.ext.napoleon',
    'myst_parser',
]


language = 'en'
html_theme = 'sphinx_material'
html_title = "Ecliptic Seasons"
html_theme_options = {
    'repo_url': 'https://github.com/joe-vettek/Ecliptic-Seasons',
    'repo_name': 'Ecliptic Seasons'
}


html_static_path = ['_static']

exclude_patterns = ['_build', 'Thumbs.db', '.DS_Store']

source_suffix = {
    '.rst': 'restructuredtext',
    '.md': 'markdown',
}

master_doc = 'index'
