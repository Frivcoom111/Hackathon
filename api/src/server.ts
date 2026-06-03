import { appBuild } from "./app";
import { env } from "./config/env";

const main = async (): Promise<void> => {
  const app = await appBuild();

  app.listen(env.PORT, () => console.log(`Servidor rodando: http://localhost:${env.PORT}`));
};

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
