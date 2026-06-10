import bcrypt from "bcrypt";
import { env } from "../../config/env";

export const generateHash = async (value: string): Promise<string> => {
  return await bcrypt.hash(value, env.SALT);
};

export const compareHash = async (value: string, hash: string): Promise<boolean> => {
  return await bcrypt.compare(value, hash);
};
