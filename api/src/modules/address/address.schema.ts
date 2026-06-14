import type { z } from "../../lib/zod";
import { addressSchema } from "../../shared/schemas/common.schema";

export { addressSchema };

export type AddressInput = z.infer<typeof addressSchema>;
