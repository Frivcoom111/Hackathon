import express from "express";

export const appBuild = async () => {
    const app = express();
    app.use(express.json())

    return app
}

