boolean checkRolloutStatus(String controllerKind, String appName, String namespace) {
    boolean isRolloutComplete = false
    try {
        steps.timeout(time: Constants.ROLLOUT_STATUS_TIMEOUT, unit: 'SECONDS') {
            steps.sh "kubectl rollout status ${controllerKind}/${appName} --namespace=${namespace}"
            isRolloutComplete = true  // Mark as complete if this point is reached
        }
    } catch (Exception e) {
        steps.echo("Rollout status check timed out.")
        isRolloutComplete = false  // Indicate the rollout is not complete
    }
    return isRolloutComplete
}

boolean checkRolloutStatus(String controllerKind, String appName, String namespace) {
    boolean isRolloutComplete = false
    try {
        steps.timeout(time: Constants.ROLLOUT_STATUS_TIMEOUT, unit: 'SECONDS') {
            steps.sh "kubectl rollout status ${controllerKind}/${appName} --namespace=${namespace}"
            isRolloutComplete = true  // Mark as complete if this point is reached
        }
    } catch (Exception e) {
        steps.echo("Rollout status check timed out.")
        isRolloutComplete = false  // Indicate the rollout is not complete
    }
    return isRolloutComplete
}
