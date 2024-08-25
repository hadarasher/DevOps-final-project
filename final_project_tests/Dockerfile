FROM bitnami/python

WORKDIR /app

COPY requirements.txt /app/requirements.txt
RUN pip install -r requirements.txt

COPY tests/ /app/tests/

ENTRYPOINT ["pytest", "/app/tests/"]
