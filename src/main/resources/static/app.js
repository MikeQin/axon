let actionInProgress = false
let amountInputIssue
let issueButton
let idInputRedeem
let amountInputRedeem
let redeemButton
let notification
let notificationText
let notificationButton
let tableSource

function setDomElements() {
    amountInputIssue = document.getElementById("amount-input-issue")
    issueButton = document.getElementById("issue-button")
    idInputRedeem = document.getElementById("id-input-redeem")
    amountInputRedeem = document.getElementById("amount-input-redeem")
    redeemButton = document.getElementById("redeem-button")
    notification = document.getElementById("notification")
    notificationText = document.getElementById("notification-text")
    notificationButton = document.getElementById("notification-button")
}

function maybeSwitchIssueState() {
    if (issueButton.disabled && !actionInProgress && amountInputIssue.value !== "") {
        issueButton.disabled = false
    } else if (amountInputIssue.value === "") {
        issueButton.disabled = true
    }
}

function maybeSwitchRedeemState() {
    if (redeemButton.disabled && !actionInProgress && idInputRedeem.value !== "" && amountInputRedeem.value !== "") {
        redeemButton.disabled = false
    } else if (idInputRedeem.value === "" || amountInputRedeem.value === "") {
        redeemButton.disabled = true
    }
}

function maybeSwitchAll(){
    maybeSwitchIssueState()
    maybeSwitchRedeemState()
}

function disableAllButtons() {
    issueButton.disabled = true
    redeemButton.disabled = true
}

function hideNotification() {
    notification.style.visibility = "hidden"
}

function removeColorClassesFromNotification() {
    notification.classList.remove("is-success", "is-info", "is-danger")
}

async function handleResult(result) {
    actionInProgress = false
    hideNotification()
    removeColorClassesFromNotification()
    if (result["isSuccess"] === true) {
        notification.classList.add("is-success")
        notificationText.innerHTML = "Success"
    } else {
        notification.classList.add("is-danger")
        notificationText.innerHTML = result["error"]
    }
    notification.style.visibility = ""
    maybeSwitchAll()
}


async function issueCard() {
    actionInProgress = true
    disableAllButtons()
    const amount = amountInputIssue.value
    amountInputIssue.value = ""
    const response = await fetch("/giftcard/issue?amount=" + amount, {
        method: "POST"
    });
    response.json().then(result => handleResult(result))
}

async function redeemCard() {
    actionInProgress = true
    disableAllButtons()
    const id = idInputRedeem.value
    const amount = amountInputRedeem.value
    idInputRedeem.value = ""
    amountInputRedeem.value = ""
    const response = await fetch("/giftcard/redeem?id=" + id + "&amount=" + amount, {
        method: "POST"
    });
    response.json().then(result => {
        handleResult(result)
    })
}

async function updateTable(maxRows) {
    if (tableSource !== undefined){
        tableSource.close()
    }
    let tableIds = []
    const tableBody = document.getElementById("table-body")
    tableBody.innerHTML = ""
    const response = await fetch("/giftcard/query");
    const cardArr = await response.json();
    for (let cardData of cardArr) {
	    const cardId = "card_" + cardData["id"]
	    if (tableIds.includes(cardId)) {
	        document.getElementById(cardId).remove()
	        tableIds = tableIds.filter(item => item !== cardId)
	    } else if (tableIds.length >= maxRows) {
	        const toRemove = tableIds.pop()
	        document.getElementById(toRemove).remove()
	    }
	    tableIds.unshift(cardId)
	    const row = tableBody.insertRow(0)
	    row.id = cardId
	    const cell1 = row.insertCell(0)
	    const cell2 = row.insertCell(1)
	    const cell3 = row.insertCell(2)
	    const cell4 = row.insertCell(3)
	    const cell5 = row.insertCell(4)
	    cell1.innerHTML = cardData["id"]
	    cell2.innerHTML = cardData["initialValue"]
	    cell3.innerHTML = cardData["remainingValue"]
	    cell4.innerHTML = new Date(cardData["issued"]).toLocaleString()
	    cell5.innerHTML = new Date(cardData["lastUpdated"]).toLocaleString()
	}
}

function setListeners() {
    notificationButton.addEventListener("click", () => {
        void hideNotification()
    });
    issueButton.addEventListener("click", () => {
        void issueCard();
    });
    redeemButton.addEventListener("click", () => {
        void redeemCard();
    });
    amountInputIssue.addEventListener("keyup", () => {
        maybeSwitchIssueState()
    })
    idInputRedeem.addEventListener("keyup", () => {
        maybeSwitchRedeemState()
    })
    amountInputRedeem.addEventListener("keyup", () => {
        maybeSwitchRedeemState()
    })
    addEventListener("paste", () => {
        setTimeout(maybeSwitchAll, 10)
    })
    document.getElementById("table-size-select").addEventListener("change", event => {
        void updateTable(event.target.value)
    })
}

window.addEventListener("load", () => {
    setDomElements()
    setListeners()
    void updateTable(20)
})