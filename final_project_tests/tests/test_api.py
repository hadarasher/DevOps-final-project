import logging
import requests
import pytest
from datetime import datetime
import time

BASE_URL='http://final-project:8080/api/jobs'
logging.basicConfig(level=logging.INFO,format='%(asctime)s -%(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def log_response(response)->None:
    """
    Utility function to log thr HTTP response's status code and its body
    :param response: an HTTP response object
    :return:
    """
    try:
        response_body = response.json()
    except ValueError:
        response_body = "No JSON Response"
    logger.info(f"Response status: {response.status_code}")
    logger.info(f"Response body: {response_body}")

def wait_for_service(url, timeout=60):
    start_time = time.time()
    while True:
        try:
            response = requests.get(url)
            if response.status_code == 200:
                print(f"Service is up and running.")
                break
        except requests.ConnectionError:
            pass
        if time.time() - start_time > timeout:
            raise Exception(f"Service did not become available within {timeout} seconds.")
        time.sleep(5)

@pytest.fixture(scope='module')
def setup():
    print("\nSetup for the module")
    job_data = {
        'jobName': 'demo_job2',
        'status': 'demo_status',
        'createdAt': datetime.now().isoformat(),
        'updatedAt': datetime.now().isoformat(),
        'jobType': 'BUILD',
        'sensitiveData': 'top secret'
    }
    wait_for_service("http://localhost:8080/actuator/health")
    yield job_data
    print("\nTeardown for the module")


# def print_response(response):
#     print(f'Status code: {response.status_code}')
#     print(f'Response body: {response.json()}')

def test_get_all_jobs(setup):
    response = requests.get(BASE_URL)
    log_response(response)
    assert response.status_code == 200, "Failed to get all jobs"
    assert isinstance(response.json(), list), "Response body doesn't contain a list"

def test_create_job(setup):
    job_data = setup

    response = requests.post(BASE_URL, json=job_data)
    log_response(response)
    assert response.status_code == 201, f"Failed to create job"
    data = response.json()
    assert data['jobName'] == job_data['jobName'], "jobName does not match"
    assert data['status'] == job_data['status'], "status does not match"
    assert data['jobType'] == job_data['jobType'], "jobType does not match"
    assert 'id' in data, "id does not exist"

def test_get_job_by_id(setup):
    job_data = setup
    post_response = requests.post(BASE_URL, json=job_data)
    log_response(post_response)

    assert post_response.status_code == 201, f"Failed to create job"
    created_job = post_response.json()
    created_job_id = created_job['id']

    assert created_job_id is not None, "Job ID is None, cannot proceed with GET request"

    get_response = requests.get(f"{BASE_URL}/{created_job_id}")
    log_response(get_response)
    assert get_response.status_code == 200, f"Failed to get job"

    retrieved_job = get_response.json()
    assert retrieved_job['jobName'] == job_data['jobName'], "jobName does not match"
    assert retrieved_job['status'] == job_data['status'], "status does not match"
    assert retrieved_job['jobType'] == job_data['jobType'], "jobType does not match"
    assert retrieved_job['id'] == created_job_id, "id does not exist"

def test_update_job_status (setup):
    job_data = setup
    post_response = requests.post(BASE_URL, json=job_data)
    log_response(post_response)

    assert post_response.status_code == 201, f"Failed to create job"
    created_job = post_response.json()
    created_job_id = created_job['id']

    job_data['status'] = 'UPDATED_STATUS'
    update_response = requests.put(f"{BASE_URL}/{created_job_id}", json=job_data)
    log_response(update_response)

    assert update_response.status_code == 200, f"Failed to update job"
    updated_job = update_response.json()
    assert updated_job['status'] == job_data['status'], "status was not updated"

def test_delete_job (setup):
    job_data = setup
    post_response = requests.post(BASE_URL, json=job_data)
    log_response(post_response)

    assert post_response.status_code == 201, f"Failed to create job"
    created_job = post_response.json()
    created_job_id = created_job['id']

    delete_response = requests.delete(f"{BASE_URL}/{created_job_id}")
    log_response(delete_response)

    assert delete_response.status_code == 204, f"Failed to delete job"

    get_response = requests.get(f"{BASE_URL}/{created_job_id}")
    assert get_response.status_code == 404, f"Job still exists after deletion"


if __name__ == '__main__':
    pytest.main()