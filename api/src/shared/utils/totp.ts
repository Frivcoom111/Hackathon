import { generateSecret, generateURI, verify } from "otplib";
import QRCode from "qrcode";

const ISSUER = "Hackathon API";

export const generateTotpSecret = (): string => generateSecret();

export const buildOtpAuthUrl = (email: string, secret: string): string =>
  generateURI({ issuer: ISSUER, label: email, secret });

export const generateQrCodeDataUrl = (otpauthUrl: string): Promise<string> => QRCode.toDataURL(otpauthUrl);

export const verifyTotp = async (code: string, secret: string): Promise<boolean> => {
  const result = await verify({ token: code, secret });
  return result.valid;
};
