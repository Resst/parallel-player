import lottieWeb from 'https://cdn.skypack.dev/lottie-web';

class AudioPlayer extends HTMLElement {
    constructor() {
        super();
        const template = document.querySelector('template');
        const templateContent = template.content;
        const shadow = this.attachShadow({mode: 'open'});
        shadow.appendChild(templateContent.cloneNode(true));
    }

    connectedCallback() {
        everything(this);
    }
}

const everything = function (element) {
    const shadow = element.shadowRoot;

    const audioPlayerContainer = shadow.getElementById('audio-player-container');
    const playIconContainer = shadow.getElementById('play-icon');
    const shuffleIconContainer = shadow.getElementById('shuffle-icon');
    const repeatIconContainer = shadow.getElementById('repeat-icon');
    const seekSlider = shadow.getElementById('seek-slider');
    const volumeSlider = shadow.getElementById('volume-slider');
    const muteIconContainer = shadow.getElementById('mute-icon');
    const menuIconContainer = shadow.getElementById('menu-icon');
    const myDropdownContainer = shadow.getElementById('myDropdown');
    const audio = shadow.querySelector('audio');
    const durationContainer = shadow.getElementById('duration');
    const currentTimeContainer = shadow.getElementById('current-time');
    const outputContainer = shadow.getElementById('volume-output');
    const playerNameContainer = shadow.getElementById('player-name');

    let plId = element.getAttribute('player-id');
    let plLayer = layers[plId];
    let isPaused = true;
    let shuffled = !layers[plId].random;
    let repeated = !layers[plId].cycled;
    let muteState = 'unmute';
    let menuOpened = false;
    let raf = null;

    let plDataLinkToSend =
        isOwner ? `${linkToSend}/${plLayer.id}/owner` : canModify ? `${linkToSend}/${plId}/moderator` : '';
    let plDataLinkToSubscribe =
        isOwner ? `${linkToSubscribe}/${plLayer.id}/owner` : `${linkToSubscribe}/${plId}/all`;

    isModifying = false;
    initiateTracksInLayer();

    setShuffled(layers[plId].random);
    setRepeated(layers[plId].cycled);

    waitForSocketConnection(function () {
        if (isOwner) {
            ownerControls();
        }
        subscribe();
    });

    if (!(plLayer.nowPlaying === null)) {
        audio.src = plLayer.nowPlaying.address;
        playerNameContainer.textContent = `${plLayer.name} - ${plLayer.nowPlaying.name}`;
    } else
        playerNameContainer.textContent = plLayer.name;


    const playAnimation = lottieWeb.loadAnimation({
        container: playIconContainer,
        path: 'https://maxst.icons8.com/vue-static/landings/animated-icons/icons/pause/pause.json',
        renderer: 'svg',
        loop: false,
        autoplay: false,
        name: "Play Animation",
    });

    const muteAnimation = lottieWeb.loadAnimation({
        container: muteIconContainer,
        path: 'https://maxst.icons8.com/vue-static/landings/animated-icons/icons/mute/mute.json',
        renderer: 'svg',
        loop: false,
        autoplay: false,
        name: "Mute Animation",
    });

    const menuAnimation = lottieWeb.loadAnimation({
        container: menuIconContainer,
        path: 'https://maxst.icons8.com/vue-static/landings/animated-icons/icons/expand/expand.json',
        renderer: 'svg',
        loop: false,
        autoplay: false,
        name: "Menu Animation",
    });


    playAnimation.goToAndStop(14, true);

    const whilePlaying = () => {
        seekSlider.value = Math.floor(audio.currentTime);
        currentTimeContainer.textContent = calculateTime(seekSlider.value);
        audioPlayerContainer.style.setProperty('--seek-before-width', `${seekSlider.value / seekSlider.max * 100}%`);
        raf = requestAnimationFrame(whilePlaying);
    }

    const showRangeProgress = (rangeInput) => {
        if (rangeInput === seekSlider) audioPlayerContainer.style.setProperty('--seek-before-width', rangeInput.value / rangeInput.max * 100 + '%');
        else audioPlayerContainer.style.setProperty('--volume-before-width', rangeInput.value / rangeInput.max * 100 + '%');
    }

    const calculateTime = (secs) => {
        const minutes = Math.floor(secs / 60);
        const seconds = Math.floor(secs % 60);
        const returnedSeconds = seconds < 10 ? `0${seconds}` : `${seconds}`;
        return `${minutes}:${returnedSeconds}`;
    }

    const displayDuration = () => {
        durationContainer.textContent = calculateTime(audio.duration);
    }

    const setSliderMax = () => {
        seekSlider.max = Math.floor(audio.duration);
    }

    const displayBufferedAmount = () => {
        const bufferedAmount = Math.floor(audio.buffered.end(audio.buffered.length - 1));
        audioPlayerContainer.style.setProperty('--buffered-width', `${(bufferedAmount / seekSlider.max) * 100}%`);
    }

    if (audio.readyState > 0) {
        displayDuration();
        setSliderMax();
        displayBufferedAmount();
    } else {
        audio.addEventListener('loadedmetadata', () => {
            displayDuration();
            setSliderMax();
            displayBufferedAmount();
        });
    }

    addEventListeners();

    isModifying = true;

    function addEventListeners() {
        playIconContainer.addEventListener('click', () => {
            if (canModify)
                setPaused(!isPaused);
        });

        shuffleIconContainer.addEventListener('click', () => {
            if (canModify)
                setShuffled(!shuffled);
        });

        repeatIconContainer.addEventListener('click', () => {
            if (canModify)
                setRepeated(!repeated);
        });

        muteIconContainer.addEventListener('click', () => {
            if (muteState === 'unmute') {
                muteAnimation.playSegments([0, 15], true);
                audio.muted = true;
                muteState = 'mute';
            } else {
                muteAnimation.playSegments([15, 25], true);
                audio.muted = false;
                muteState = 'unmute';
            }
        });

        menuIconContainer.addEventListener('click', () => {
            if (!menuOpened) {
                menuAnimation.playSegments([0, 15], true);
                myDropdownContainer.style.display = "block";
            } else {
                menuAnimation.playSegments([15, 25], true);
                myDropdownContainer.style.display = "none";
            }
            menuOpened = !menuOpened;
        });

        audio.addEventListener('progress', displayBufferedAmount);

        audio.addEventListener('ended', nextTrack);

        seekSlider.addEventListener('input', (e) => {
            if (canModify) {
                showRangeProgress(e.target);
                currentTimeContainer.textContent = calculateTime(seekSlider.value);
                if (!audio.paused) {
                    cancelAnimationFrame(raf);
                }
            }
        });

        seekSlider.addEventListener('change', () => {
            if (canModify) {
                audio.currentTime = seekSlider.value;
                if (!audio.paused) {
                    requestAnimationFrame(whilePlaying);
                }
                sendToWebSocket({'time': seekSlider.value});
            }
        });

        volumeSlider.addEventListener('input', (e) => {
            const value = e.target.value;
            showRangeProgress(e.target);
            outputContainer.textContent = value;
            audio.volume = value / 100;
        });
    }

    function setPaused(newPaused) {
        if (audio.paused === newPaused)
            return;
        if (newPaused) {
            audio.pause();
            playAnimation.playSegments([0, 14], true);
            cancelAnimationFrame(raf);
        } else {
            audio.play();
            playAnimation.playSegments([14, 27], true);
            requestAnimationFrame(whilePlaying);
        }
        isPaused = newPaused;
        sendToWebSocket({'paused': isPaused});
    }

    function setShuffled(shuff) {
        if (shuffled === shuff)
            return;
        shuffled = shuff;
        shuffleIconContainer.style.backgroundImage = shuffled ?
            'url("https://img.icons8.com/fluency/96/000000/shuffle.png")' :
            'url("https://img.icons8.com/dotty/80/000000/shuffle.png")';
        sendToWebSocket({'shuffled': shuffled});
    }

    function setRepeated(rep) {
        if (repeated === rep)
            return;
        repeated = rep;
        audio.loop = repeated;
        repeatIconContainer.style.backgroundImage = repeated ?
            'url("https://img.icons8.com/fluency/96/000000/repeat.png")' :
            'url("https://img.icons8.com/dotty/80/000000/repeat.png")';
        sendToWebSocket({'repeated': repeated});
    }

    function initiateTracksInLayer() {
        plLayer.tracks.forEach(
            track => {
                let roomLink = document.createElement("a");
                roomLink.href = "#";
                roomLink.innerText = track.name;
                myDropdownContainer.append(roomLink);
                if (canModify) {
                    roomLink.addEventListener('click', () => setTrack(track))
                }
            }
        )
    }

    function setTrack(track) {
        if (plLayer.nowPlaying === track)
            return;
        plLayer.nowPlaying = track;
        audio.src = track.address;
        if (!isPaused)
            audio.play();
        playerNameContainer.textContent = `${plLayer.name} - ${plLayer.nowPlaying.name}`;
        sendToWebSocket({'nowPlayingId': track.id});
    }

    function nextTrack(){
        let newTrackId = 0;
        console.log(shuffled);
        if (shuffled) {
            newTrackId = Math.floor(Math.random() * (plLayer.tracks.length - 1));
            console.log(newTrackId);
            newTrackId += newTrackId >= plLayer.nowPlaying.id ? 1 : 0
            console.log(newTrackId);
        }else{
            newTrackId = plLayer.nowPlaying.id + 1;
            if (newTrackId >= plLayer.tracks.length)
                newTrackId = 0;
        }
        setTrack(plLayer.tracks[newTrackId]);
    }

    function ownerControls() {
        setInterval(() => synchronizeViaWebSocket({
            'paused': isPaused,
            'shuffled': shuffled,
            'repeated': repeated,
            'time': audio.currentTime,
            'nowPlayingId': plLayer.nowPlaying === null ? null : plLayer.nowPlaying.id
        }), 2000);
    }

    function subscribe() {
        stompClient.subscribe(plDataLinkToSubscribe, function (newTime) {
            receiveFromWebSocket(JSON.parse(newTime.body));
        });
    }

    function receiveFromWebSocket(data) {
        if (!isOwner)
            isModifying = false;

        if (!(data.paused === null))
            setPaused(data.paused);
        if (!(data.shuffled === null))
            setShuffled(data.shuffled);
        if (!(data.repeated === null))
            setRepeated(data.repeated);
        if (!(data.time === null)) {
            if (Math.abs(data.time - audio.currentTime) > 2)
                audio.currentTime = data.time;
        }
        if (!(data.nowPlayingId === null))
            setTrack(plLayer.tracks[data.nowPlayingId]);
        isModifying = true;
    }

    function sendToWebSocket(data) {
        if (canModify && isModifying) {
            waitForSocketConnection(() =>
                stompClient.send(plDataLinkToSend, {}, JSON.stringify(data)));
        }
    }

    function synchronizeViaWebSocket(data){
        waitForSocketConnection(() =>
            stompClient.send(`${plDataLinkToSend}-update`, {}, JSON.stringify(data)));
    }
}

customElements.define('audio-player', AudioPlayer);
