import pandas as pd
import nltk
import ssl
from nltk.tokenize import word_tokenize

nltk.download('punkt')
# Use VADER to train a sentiment analysis model
from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer

import plotly.graph_objs as go

text_data = [
    "I love this product!",
    "I hate this product.",
    "This product is okay.",
    "I'm so happy!",
    "I'm so sad.",
]

# Create a pandas DataFrame with the text data
df = pd.DataFrame({"text": text_data})

def preprocess_text(text):
    tokens = word_tokenize(text.lower())
    tokens = [t for t in tokens if t.isalpha()]
    return ' '.join(tokens)

df['preprocessed_text'] = df['text'].apply(preprocess_text)

sia = SentimentIntensityAnalyzer()

def analyze_sentiment(text):
    sentiment_scores = sia.polarity_scores(text)
    return sentiment_scores

df['sentiment_score'] = df['preprocessed_text'].apply(analyze_sentiment)

fig = go.Figure(data=[
    go.Bar(x=df['sentiment_score'], y=df['preprocessed_text'], name='Sentiment'),
])

fig.update_layout(
    title='Sentiment Analysis Dashboard',
    xaxis_title='Sentiment Score',
    yaxis_title='Preprocessed Text',
)

fig.show()
