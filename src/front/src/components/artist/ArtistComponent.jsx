import React, { Component, useEffect, useState } from "react";
import BackendService from "../../services/BackendService";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChevronLeft, faSave } from "@fortawesome/free-solid-svg-icons";
import { alertActions } from "../../utils/Rdx";
import { connect } from "react-redux";
import { Form } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";

const ArtistComponent = (props) => {
  const params = useParams();
  const [id, setId] = useState(params.id);
  const [name, setName] = useState("");
  const [country, setCountry] = useState("");
  const [century, setCentury] = useState("");
  const [hidden, setHidden] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (parseInt(id) !== -1) {
      BackendService.retrieveArtist(id)
        .then((resp) => {
          console.log(resp.data);
          setName(resp.data.name);
        })
        .catch(() => setHidden(true));
    }
  }, []); // [] needed to call useEffect only once during component initialization
  //  this is necessary so that the value from the database is not written to the name state each time

  const onSubmit = (event) => {
    event.preventDefault();
    event.stopPropagation();
    let err = null;
    if (!name) err = "Название художника должно быть указано";
    if (err) props.dispatch(alertActions.error(err));
    let artist = { id, name, country, century };

    if (parseInt(artist.id) === -1) {
      BackendService.createArtist(artist)
        .then(() => navigate(`/artists`))
        .catch(() => {});
    } else {
      BackendService.updateArtist(artist)
        .then(() => navigate(`/artists`))
        .catch(() => {});
    }
  };

  if (hidden) return null;
  return (
    <div className="m-4">
      <div className=" row my-2 mr-0">
        <h3>Художник</h3>
        <div className="d-flex justify-content-end">
          <button
            className="btn btn-outline-secondary ml-auto btn-back"
            onClick={() => navigate(`/artists`)}
          >
            <FontAwesomeIcon icon={faChevronLeft} /> Назад
          </button>
        </div>
      </div>
      <Form onSubmit={onSubmit}>
        <Form.Group>
          <Form.Label>Имя</Form.Label>
          <Form.Control
            type="text"
            className="mb-3"
            placeholder="Введите название художника"
            onChange={(e) => {
              setName(e.target.value);
            }}
            value={name}
            name="name"
            autoComplete="off"
          />
          <Form.Label>Страна</Form.Label>
          <Form.Control
            type="text"
            className="mb-3"
            placeholder="Введите ID страны художника"
            onChange={(e) => {
              setCountry(e.target.value);
            }}
            value={country}
            name="country"
            autoComplete="off"
          />
          <Form.Label>Век</Form.Label>
          <Form.Control
            type="text"
            placeholder="Введите века художника"
            onChange={(e) => {
              setCentury(e.target.value);
            }}
            value={century}
            name="century"
            autoComplete="off"
          />
        </Form.Group>
        <button className="btn btn-outline-secondary mt-3" type="submit">
          <FontAwesomeIcon icon={faSave} /> Сохранить
        </button>
      </Form>
    </div>
  );
};

export default connect()(ArtistComponent);
