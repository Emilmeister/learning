

var stocksOnOnePage = 6;
var page = 1;
var withLevel = false;

setSearcherByTicker();
setLevelCheckbox();
google.charts.load('current', {'packages':['corechart']});/////////////////////////////////
google.charts.setOnLoadCallback(getStocks);//////////////////////////////////////////////
addCandlesResolutionButtons();
addBackAndNext();



var MyStock;
var candleResolution = "hour";
var currentGraphicFigi;

function getStocks(){
    jQuery.getJSON('stocks.json?'
        +'from=' +stocksOnOnePage*(page-1)
        +'&to='+ stocksOnOnePage*page
        +'&withLevel=' + withLevel, null, printStocks);


}

function printStocks(data, textStatus, jqXHR) {
    stocks = data;

    var temp = "<table>" +
        "  <tr>\n" +
        "  <th>Ticker</th>\n" +
        "  <th>Name</th>\n" +
        "  <th>Level</th>\n" +
        "  <th>Dead Cross</th>\n" +
        "  </tr>";

    for(var i = 0; i < stocks.length; i++){
        temp +=
              "<tr id ='"+ stocks[i].marketInstrument.figi +"'>"
            + "<td>" + stocks[i].marketInstrument.ticker + "</td>"
            + "<td>" + stocks[i].marketInstrument.name + "</td>"
            + "<td>" + getLevel(stocks[i].level) + "</td>"
            + "<td>" + getDeadCross(stocks[i].deadCross) +"</td>"
            + "</tr>";
    }

    temp+="</table>"



    document.getElementById("stocks").innerHTML = temp;

    for(var i = 0; i < stocks.length; i++){
        document.getElementById(stocks[i].marketInstrument.figi).addEventListener("click", loadCandles, false);
    }
}

function addBackAndNext(){

    document.getElementById("buttons").innerHTML=
        "<button id = 'backButton'>Назад</button>"
        +"<span id = 'pageNum'>"+page+"</span>"
        +"<button id = 'nextButton'>Вперед</button>";

    var back = document.getElementById("backButton");
    var next = document.getElementById("nextButton");
    if(page <= 1) back.disabled = true;
    back.addEventListener('click', backPage, false);
    next.addEventListener('click', nextPage, false);
}

function nextPage(){
    page++;
    document.getElementById("pageNum").innerHTML = page;
    var back = document.getElementById("backButton");
    if(page <= 1) back.disabled = true;
    else back.disabled = false;
    getStocks();
}

function backPage(){
    page--;
    document.getElementById("pageNum").innerHTML = page;
    var back = document.getElementById("backButton");
    if(page <= 1) back.disabled = true;
    else back.disabled = false;
    getStocks();
}

function getDeadCross(level){
    if(level == null){
        return "";
    }
    if(level =="Dead Cross"){
        return "<span class='dead-cross'>Dead Cross </span>";
    }
    return "<span class='none-cross'>None</span>"
}

function getLevel(level){
    if(level == null){
        return "";
    }
    if(level == "Upper level"){
        return "<span class ='upper-level'>Upper level </span>";
    }
    if(level == "Lower level"){
        return "<spam class ='lower-level'>Lower level </spam>";
    }
    if(level =="Coridor"){
        return "<spam class ='coridor-level'>Coridor </spam>";
    }
    return "<spam class ='none-level'>None</spam>"
}

function setSearcherByTicker(){
    document.getElementById("searcherByTickerDiv").innerHTML =
        "<input id = 'searcher' type='search' value='AAPL'>  Type ticker</input>";
    document.getElementById("searcher").addEventListener("keyup", function (e){
        if(e.code == "Enter"){
            jQuery.getJSON('stocks/graphic/ticker/'+this.value
                , null, getCandlesByTicker);



        }
    });
}

function setLevelCheckbox(){
    document.getElementById("levelCheckboxDiv").innerHTML =
        "<p><input id ='levelCheckbox' type='checkbox'>With levels</p>";
    document.getElementById("levelCheckbox").addEventListener("click", function (){
        if(this.checked == true){
            withLevel = true;
        }else withLevel = false;
        page = 1;
        document.getElementById("pageNum").innerHTML = page;
        document.getElementById("backButton").disabled = true;
        getStocks();
    }, false);
}

function getCandlesByTicker(data, textStatus, jqXHR){
    currentGraphicFigi = data.value;
    loadCandles(currentGraphicFigi);
}








function loadCandles(figi) {
    if(this.id != null){
        figi = this.id;
    }else {
        figi = currentGraphicFigi;
    }
    currentGraphicFigi = figi;

    jQuery.getJSON('candles.json?'
        + 'figi=' + figi
        + '&candleResolution='+ candleResolution,
        null, onJsonLoad);

    function onJsonLoad(data, textStatus, jqXHR) {

        MyStock = data;
        //document.getElementsByTagName("title")[0].text = MyStock.searchMarketInstrument.name;
        //document.getElementsByTagName("title")[0].text = MyStock.level;
        document.getElementById("header").innerHTML = "<h1><a href='" +
            "https://www.tinkoff.ru/invest/stocks/"+MyStock.searchMarketInstrument.ticker+"'>"
            + MyStock.searchMarketInstrument.name
            + "<br>" + MyStock.level
            +"<a></h1>";

        drawChart();
    }
}

function drawChart(){

    var formatCandles = new Array();

    for(var i = 0; i < MyStock.candles.length; i++){
        formatCandles[i]  = [
            getTimeToGrafic(MyStock.candles[i].time),
            MyStock.candles[i].l,
            MyStock.candles[i].c,
            MyStock.candles[i].o,
            MyStock.candles[i].h
        ];
    }

    var data = google.visualization.arrayToDataTable(formatCandles, true);

    var options = {
        legend:'none',
        candlestick : {
            fallingColor : {fill:'red', stroke:'black', strokeWidth:1},
            risingColor : {fill:'green', stroke:'black', strokeWidth:1}
        }
    };

    var chart = new google.visualization.CandlestickChart(document.getElementById('chart_div'));

    chart.draw(data, options);
}

function addCandlesResolutionButtons(){
    temp = "<button id = 'hour'>hour</button>"
        +"<button id = 'day'>day</button>"
        +"<button id = 'week'>week</button>"
        +"<button id = 'month'>month</button>";
    document.getElementById("candlesResolutionDiv").innerHTML = temp;

    hour = document.getElementById("hour");
    day = document.getElementById("day");
    week = document.getElementById("week");
    month = document.getElementById("month");

    hour.addEventListener("click", getNewGraphic, false);
    day.addEventListener("click", getNewGraphic, false);
    week.addEventListener("click", getNewGraphic, false);
    month.addEventListener("click", getNewGraphic, false);


    function getNewGraphic(){
        if(this.id == 'hour') {
            candleResolution = "hour";
            hour.disbled = true;
            day.disbled = false;
            week.disbled = false;
            month.disbled = false;

        }
        if(this.id == 'day') {
            candleResolution = "day";
            hour.disbled = false;
            day.disbled = true;
            week.disbled = false;
            month.disbled = false;
        }
        if(this.id == 'week') {
            candleResolution = "week";
            hour.disbled = false;
            day.disbled = false;
            week.disbled = true;
            month.disbled = false;
        }
        if(this.id == 'month') {
            candleResolution = "month";
            hour.disbled = false;
            day.disbled = false;
            week.disbled = false;
            month.disbled = true;
        }
        loadCandles(currentGraphicFigi.id);
    }



}

function getTimeToGrafic(obj){
    if(candleResolution == "hour"){
        return obj.hour + 'h  ' + obj.dayOfMonth + ' ' + obj.month;
    }else if(candleResolution == "day"){
        return obj.dayOfMonth + ' ' + obj.month;
    }else if(candleResolution == "week"){
        return obj.dayOfMonth + ' ' + obj.month + ' ' + obj.year;
    }else if(candleResolution == "month"){
        return obj.month + ' ' + obj.year;
    }

}