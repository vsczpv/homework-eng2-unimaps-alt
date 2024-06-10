// Load HTTP module
const express = require("express")
var bodyParser = require('body-parser')
const db = require('./db')
const app = express();

var jsonParser = bodyParser.json()

const hostname = "0.0.0.0";
const port = 8000;

//

//

//test
app.get("/", function (req,res) {
    res.send("Hello")
})

//get ids
app.get("/category", async(req, res) => {
    try {
        const result = await db.query('SELECT id_categoria FROM Categoria');
        res.send(result.rows)
    } catch (err){
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
})

app.get("/service", async(req, res) => {
    try {
        const result = await db.query('SELECT id_servico FROM Servico');
        res.send(result.rows)
    } catch (err){
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
})

app.get("/service/:id_service/items", async(req, res) => {
    try {
        const id_service = + req.params.id_service
        if (id_service != NaN) {
            const result = await db.query('SELECT id_item FROM Item WHERE idf_servico = ' + id_service);
            res.send(result.rows)
        }
    } catch (err){
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
})

app.get("/service/:id_service/comments", async(req, res) => {
    try {
        const id_service = + req.params.id_service
        if (id_service != NaN) {
            const result = await db.query('SELECT id_comentario FROM Comentario WHERE idf_servico = ' + id_service);
            res.send(result.rows)
        }
    } catch (err){
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
})

//get data
app.get("/category/:id", async(req, res) => {
    try {
        const id = + req.params.id
	    console.log(id)
        if (id != NaN) {
            const result = await db.query('SELECT nome FROM Categoria WHERE id_categoria = ' + id);
            res.send(result.rows)
        }
    } catch (err){
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
})

app.get("/service/:id", async(req, res) => {
    try {
        const id = + req.params.id
        if (id != NaN) {
            const result = await db.query('SELECT idf_categoria, idf_dono, nome, nome_dono, horario_aberto, horario_fechado, horario_pico, latitute, longitude, nota, local, complemento, capacidade FROM Servico WHERE id_servico = ' + id);
            res.send(result.rows)
        }
    } catch (err){
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
})

app.get("/service/:id/banner", async(req, res) => {
    try {
        const id = + req.params.id
        if (id != NaN) {
            const result = await db.query('SELECT banner FROM Servico WHERE id_servico = ' + id);
            res.send(result.rows[0].banner)
        }
    } catch (err){
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
})

app.get("/service/:id/icon", async(req, res) => {
    try {
        const id = + req.params.id
        if (id != NaN) {
            const result = await db.query('SELECT icone FROM Servico WHERE id_servico = ' + id);
            res.send(result.rows[0].icone)
        }
    } catch (err){
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
})

app.get("/item/:id", async(req, res) => {
    try {
        const id = + req.params.id
        if (id != NaN) {
            const result = await db.query('SELECT id_item, idf_servico, nome, tipo, preco FROM Item WHERE id_item = ' + id);
            res.send(result.rows)
        }
    } catch (err){
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
})

app.get("/comment/:id", async(req, res) => {
    try {
        const id = + req.params.id
        if (id != NaN) {
            const result = await db.query('SELECT id_comentario, idf_usuario, idf_servico, nome, conteudo, avaliacao FROM Comentario JOIN Usuario ON idf_usuario = id_usuario WHERE id_comentario = ' + id);
            res.send(result.rows)
        }
    } catch (err){
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
})

//post data
app.post("/service/:id_service/comments", jsonParser, async(req, res) => {
    try {
        const id = + req.params.id_service;
        if (req.body == null) {
            res.status(400).send('Bad Request');
            return
        }
        console.log(req.body);
        const b = req.body;
        if (b.idf_usuario == null || b.idf_servico == null || b.conteudo == null || b.avaliacao == null){
            res.status(400).send('Bad Request');
            return
        }
        const query = 'INSERT INTO Comentario(idf_usuario, idf_servico, conteudo, avaliacao) VALUES(' + b.idf_usuario + ',' + b.idf_servico + ',\'' + b.conteudo + '\',\'' + b.avaliacao + '\')'
        const result = await db.query(query)
        res.status(200).send('Ok')
        
    } catch (err){
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
})

//update data
app.put("/comment/:id_comments", jsonParser, async(req, res) => {
    try {
        const id = + req.params.id_comments;
        if (id == NaN || req.body == null) {
            res.status(400).send('Bad Request');
            return
        }
        console.log(req.body);
        const b = req.body;
        if (b.conteudo == null || b.avaliacao == null){
            res.status(400).send('Bad Request');
            return
        }
        const query = 'UPDATE Comentario SET conteudo = \'' + b.conteudo + '\', avaliacao = \'' + b.avaliacao + '\' WHERE id_comentario = ' + id
        const result = await db.query(query)
        res.status(200).send('Ok')
        
    } catch (err){
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
})

//delete data
app.delete("/comment/:id_comments", jsonParser, async(req, res) => {
    try {
        const id = + req.params.id_comments;
        if (id == NaN) {
            res.status(400).send('Bad Request');
            return
        }

        const query = 'DELETE FROM Comentario WHERE id_comentario = ' + id
        const result = await db.query(query)
        res.status(200).send('Ok')
        
    } catch (err){
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
})

// Prints a log once the server starts listening
app.listen(port, hostname, function () {
  console.log(`Server running at http://${hostname}:${port}/`);
});
