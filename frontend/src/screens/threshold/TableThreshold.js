import { Fragment } from 'react';
import { IconButton, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@mui/material';
import { Riple } from 'react-loading-indicators';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

export default function TableThreshold({ handleEditOpen, handleDeleteOpen, loading, threshold }) {

    const columns = [
        { width: 80, label: 'Item', dataKey: 'itemId', numeric: true },
        { width: 80, label: 'Location', dataKey: 'locationId', numeric: true },
        { width: 50, label: 'Min Threshold', dataKey: 'minThreshold', numeric: true },
        { width: 50, label: 'Max Threshold', dataKey: 'maxThreshold', numeric: true },
        { width: 20, label: 'Action', dataKey: 'action', numeric: true },
    ];

    const rowContent = (row) => (
        <TableRow key={row.itemId}>
            {columns.map((column) => (
                <TableCell key={column.dataKey} align={column.numeric ? 'center' : 'left'}>
                    {column.dataKey === 'action' ? (
                        <>
                            <IconButton onClick={() => handleEditOpen(row)}><EditIcon /></IconButton>
                            <IconButton onClick={() => handleDeleteOpen(row)}><DeleteIcon /></IconButton>
                        </>
                    ) : row[column.dataKey]}
                </TableCell>
            ))}
        </TableRow>
    );

    return (
        <Paper style={{ height: 645, width: 1200, display: 'flex', margin: '20px' }}>
            {loading && <Riple color="#803bec" size="large" />}
            {!loading && threshold.length > 0 ? (
                <TableContainer component={Paper}>
                    <Table sx={{ borderCollapse: 'separate', tableLayout: 'fixed' }}>
                        <TableHead>
                            <TableRow>
                                {columns.map((column) => (
                                    <TableCell
                                        key={column.dataKey}
                                        align={column.numeric ? 'center' : 'left'}
                                        style={{
                                            width: column.width,
                                            backgroundColor: "black",
                                            color: "white",
                                            fontWeight: "Bold",
                                            textTransform: 'uppercase'
                                        }}
                                    >
                                        {column.label}
                                    </TableCell>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {threshold.map((row) => rowContent(row))}
                        </TableBody>
                    </Table>
                </TableContainer>
            ) : (
                !loading && <p>No threshold found.</p>
            )}
        </Paper>
    );
}
