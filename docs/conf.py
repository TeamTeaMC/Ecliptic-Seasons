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
]

language = 'en'
html_theme = 'alabaster'

html_static_path = ['_static']

exclude_patterns = ['_build', 'Thumbs.db', '.DS_Store']

source_suffix = {
    '.rst': 'restructuredtext',
    '.md': 'markdown',
}