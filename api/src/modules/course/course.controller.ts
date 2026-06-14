import type { Request, Response } from "express";
import { response } from "../../shared/utils/response";
import type { CourseService } from "./course.service";

export class CourseController {
  constructor(private readonly courseService: CourseService) {}

  async list(_req: Request, res: Response): Promise<void> {
    const courses = await this.courseService.list();
    res.status(200).json(response.success(courses));
  }
}
