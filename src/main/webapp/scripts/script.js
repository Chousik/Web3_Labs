$(document).ready(function() {
    const canvas = new Canvas('canvas');

    function updateCanvas() {
        const rElement = document.getElementById("j_id_8:rValue");
        const rValue = parseFloat(rElement.value).toFixed(1);
        canvas.redrawAll(rValue);
    }
    function updateXDisplay(){
        const xValue = document.getElementById("j_id_8:xValue");
        const xDisplay = document.getElementById("j_id_8:xValueDisplay");
        xDisplay.value = xValue.value;
    }
    function updateRDisplay(){
        const rValue = document.getElementById("j_id_8:rValue");
        const rDisplay = document.getElementById("j_id_8:rValueDisplay");
        rDisplay.value = rValue.value;
    }
    document.getElementById('canvas').addEventListener('click', (event) => {
        const { xValue, yValue } = getCanvasCoordinates(event);
        const elementX = document.getElementById("j_id_8:xValue");
        const tempX = elementX.value;
        elementX.value = xValue;

        const elementY = document.getElementById("j_id_8:Y_input");
        const tempY = elementY.value;
        elementY.value=yValue;

        document.getElementById("j_id_8:check_button").click();
        if (validateAll()){
        setTimeout(() => {
            const tableRows = document.querySelectorAll("#result_table tbody tr");
            const lastRow = tableRows[0];
            const cell = lastRow.querySelectorAll("td")[3];
            const status = cell.innerText === "true";
            canvas.drawPoint(xValue, yValue, status)
        }, 320);}
        elementX.value = tempX;
        elementY.value = tempY;
    });
    $("#j_id_8\\:check_button").click(function (){
        if (validateAll()){
            setTimeout(() => {
                const tableRows = document.querySelectorAll("#result_table tbody tr");
                const lastRow = tableRows[0];
                const cell = lastRow.querySelectorAll("td")[3];
                const status = cell.textContent.trim() === "true";
                const elementY = document.getElementById("j_id_8:Y_input");
                const yValue = parseFloat(elementY.value.replace(',', '.'));
                const elementX = document.getElementById("j_id_8:xValue");
                const xValue = parseFloat(elementX.value.replace(',', '.'));
                canvas.drawPoint(xValue, yValue, status);
            }, 320);
        }
    })
    updateCanvas();
    $("#j_id_8\\:rSlider").on("slidechange", function() {
        updateCanvas();
        updateRDisplay()
    });
    $("#j_id_8\\:xSlider").on("slidechange", function() {
        updateXDisplay()
    });
    function getCanvasCoordinates(event) {
        const canvas = document.getElementById('canvas');
        const rect = canvas.getBoundingClientRect();

        const x = event.clientX - rect.left;
        const y = event.clientY - rect.top;
        const xValue = ((x - rect.width / 2) / 24).toFixed(1);
        const yValue = (-(y - rect.height / 2) / 24).toFixed(1);
        return { xValue, yValue };
    }
});