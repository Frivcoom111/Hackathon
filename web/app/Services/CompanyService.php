<?php

declare(strict_types=1);

namespace App\Services;

use App\Http\ApiException;
use App\Models\Company;

final class CompanyService extends AbstractApiService
{
    /** @return array<int, Company> */
    public function list(): array
    {
        if (!$this->mockEnabled()) {
            return [];
        }

        return Company::collection(MockData::companies());
    }
}
